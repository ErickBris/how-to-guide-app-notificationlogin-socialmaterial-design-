package com.ristana.how_to.ui.activity;

import android.app.ProgressDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ristana.how_to.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.ristana.how_to.api.apiClient;
import com.ristana.how_to.api.apiRest;
import com.ristana.how_to.entity.ApiResponse;

public class  ContactActivity extends AppCompatActivity {

    private EditText support_input_email;
    private EditText support_input_message;
    private EditText support_input_name;
    private TextInputLayout support_input_layout_email;
    private TextInputLayout support_input_layout_message;
    private TextInputLayout support_input_layout_name;
    private Button support_button;
    private ProgressDialog register_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        initView();
        initAction();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

        }
        return super.onOptionsItemSelected(item);
    }
    public void initView(){
        this.support_input_email=(EditText) findViewById(R.id.support_input_email);
        this.support_input_message=(EditText) findViewById(R.id.support_input_message);
        this.support_input_name=(EditText) findViewById(R.id.support_input_name);
        this.support_input_layout_email=(TextInputLayout) findViewById(R.id.support_input_layout_email);
        this.support_input_layout_message=(TextInputLayout) findViewById(R.id.support_input_layout_message);
        this.support_input_layout_name=(TextInputLayout) findViewById(R.id.support_input_layout_name);
        this.support_button=(Button) findViewById(R.id.support_button);
    }
    public void initAction(){
        this.support_input_email.addTextChangedListener(new ContactActivity.SupportTextWatcher(this.support_input_email));
        this.support_input_name.addTextChangedListener(new ContactActivity.SupportTextWatcher(this.support_input_name));
        this.support_input_message.addTextChangedListener(new ContactActivity.SupportTextWatcher(this.support_input_message));
        this.support_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }
    public void submit(){
        if (!validateEmail()) {
            return;
        }
        if (!validatName()) {
            return;
        }
        if (!validatMessage()) {
            return;
        }
        register_progress= ProgressDialog.show(this,null,getString(R.string.operation_progress));

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.addSupport(support_input_email.getText().toString(),support_input_name.getText().toString(),support_input_message.getText().toString());
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(ContactActivity.this, getResources().getString(R.string.message_sended), Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(ContactActivity.this, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
                }
                register_progress.dismiss();
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                register_progress.dismiss();
                Toast.makeText(ContactActivity.this, getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean validatName() {
        if (support_input_name.getText().toString().trim().isEmpty() || support_input_name.getText().length()  < 3 ) {
            support_input_layout_name.setError(getString(R.string.error_short_value));
            requestFocus(support_input_name);
            return false;
        } else {
            support_input_layout_name.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validatMessage() {
        if (support_input_message.getText().toString().trim().isEmpty() || support_input_message.getText().length()  < 3 ) {
            support_input_layout_message.setError(getString(R.string.error_short_value));
            requestFocus(support_input_message);
            return false;
        } else {
            support_input_layout_message.setErrorEnabled(false);
        }

        return true;
    }
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private boolean validateEmail() {
        String email = support_input_email.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            support_input_layout_email.setError(getString(R.string.error_mail_valide));
            requestFocus(support_input_email);
            return false;
        } else {
            support_input_layout_email.setErrorEnabled(false);
        }
        return true;
    }
    private class SupportTextWatcher implements TextWatcher {
        private View view;
        private SupportTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.support_input_email:
                    validateEmail();
                    break;
                case R.id.support_input_name:
                    validatName();
                    break;
                case R.id.support_input_message :
                    validatMessage();
                    break;
            }
        }
    }
}
