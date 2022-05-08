package com.ristana.how_to.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ristana.how_to.R;
import com.ristana.how_to.entity.ApiResponse;
import com.ristana.how_to.manager.PrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.ristana.how_to.api.apiClient;
import com.ristana.how_to.api.apiRest;
public class EditActivity extends AppCompatActivity {

    private EditText edit_text_name;
    private TextInputLayout input_layout_edit_text_name;
    private PrefManager prf;
    private Button button_edit_info;
    private ProgressDialog login_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prf= new PrefManager(getApplicationContext());
        initView();
        initAction();
    }

    private void initView() {
        setContentView(R.layout.activity_edit);
        this.edit_text_name=(EditText) findViewById(R.id.edit_text_name);
        this.input_layout_edit_text_name=(TextInputLayout) findViewById(R.id.input_layout_edit_text_name);
        this.button_edit_info=(Button) findViewById(R.id.button_edit_info);
        if (prf.getString("LOGGED").toString().equals("TRUE")){
            this.edit_text_name.setText(prf.getString("NAME_USER"));
        }else{
            finish();
        }
    }

    private void initAction() {
        this.edit_text_name.addTextChangedListener(new EditActivity.EditTextWatcher(edit_text_name));
        this.button_edit_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }
    private void submitForm() {
        if (!validateName()) {
            return;
        }
        login_progress= ProgressDialog.show(this,null,getString(R.string.operation_progress));
        String id_ser=  prf.getString("ID_USER");
        String key_ser=  prf.getString("TOKEN_USER");
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.editName(id_ser,edit_text_name.getText().toString(),key_ser);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.body()!=null){
                    int code = response.body().getCode();
                    String message=response.body().getMessage();
                    if (code==200) {
                        prf.setString("NAME_USER", edit_text_name.getText().toString());
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(EditActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),getString(R.string.no_connexion),Toast.LENGTH_SHORT).show();
                }
                login_progress.dismiss();

            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                login_progress.dismiss();

            }
        });
    }
    private boolean validateName() {
        if (edit_text_name.getText().toString().trim().isEmpty() || edit_text_name.getText().length()  < 3 ) {
            input_layout_edit_text_name.setError(getString(R.string.error_short_value));
            requestFocus(edit_text_name);
            return false;
        } else {
            input_layout_edit_text_name.setErrorEnabled(false);
        }
        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private class EditTextWatcher implements TextWatcher {
        private View view;
        private EditTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edit_text_name:
                    validateName();
                    break;

            }
        }
    }
}
