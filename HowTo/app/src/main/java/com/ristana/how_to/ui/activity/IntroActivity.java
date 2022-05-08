package com.ristana.how_to.ui.activity;

import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ristana.how_to.R;
import com.ristana.how_to.entity.ApiResponse;
import com.ristana.how_to.manager.PrefManager;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.ristana.how_to.api.apiClient;
import com.ristana.how_to.api.apiRest;

public class IntroActivity extends AppCompatActivity {
    private ProgressBar intro_progress;
    private RelativeLayout activity_intro;
    private PrefManager prf;
    private TextView text_view_app_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        prf= new PrefManager(getApplicationContext());
        initView();
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // If you want to modify a view in your Activity
                IntroActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (prf.getString("LOGGED").contains("TRUE")){
                            checkAccount();
                        }else{
                            Intent intent = new Intent(IntroActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        }, 5000);
    }
    private  void initView(){
        setContentView(R.layout.activity_intro);
        this.intro_progress=(ProgressBar) findViewById(R.id.intro_progress);
        this.activity_intro=(RelativeLayout) findViewById(R.id.activity_intro);
        intro_progress.setVisibility(View.VISIBLE);
        this.text_view_app_version=(TextView) findViewById(R.id.text_view_app_version);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(),0);
            String version = pInfo.versionName;
            text_view_app_version.setText("v "+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void checkAccount() {

        intro_progress.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.check(prf.getString("ID_USER"),prf.getString("TOKEN_USER"));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                if (response.isSuccessful()){
                    if (response.body().getCode()==200){
                        Intent intent = new Intent(IntroActivity.this,MainActivity.class);
                        startActivity(intent);
                    }else if(response.body().getCode()==500){
                        logout();
                        Intent intent = new Intent(IntroActivity.this,MainActivity.class);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(IntroActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                }else{
                    Intent intent = new Intent(IntroActivity.this,MainActivity.class);
                    startActivity(intent);
                }

            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Intent intent = new Intent(IntroActivity.this,MainActivity.class);
                startActivity(intent);
                intro_progress.setVisibility(View.INVISIBLE);

            }
        });

    }
    public      void logout(){
        PrefManager prf= new PrefManager(getApplicationContext());
        prf.remove("ID_USER");
        prf.remove("SALT_USER");
        prf.remove("TOKEN_USER");
        prf.remove("NAME_USER");
        prf.remove("TYPE_USER");
        prf.remove("USERNAME_USER");
        prf.remove("URL_USER");
        prf.remove("LOGGED");
        Toast.makeText(getApplicationContext(),getString(R.string.message_logout_desibaled),Toast.LENGTH_LONG).show();
    }
    @Override
    public  void onPause(){
        super.onPause();
        finish();
    }
}
