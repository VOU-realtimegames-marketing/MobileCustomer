package com.example.customer.controller.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.customer.Config.Config;
import com.example.customer.R;
import com.example.customer.utils.Utils;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import vou.proto.GatewayGrpc;
import vou.proto.RpcLoginUser;
import vou.proto.UserOuterClass;


public class LogInActivity extends Activity implements TextWatcher{
    EditText edtEmail,edtPassword;
    TextView txtErrorEmail,txtErrorMsg;
    Button btnSignUp,btnLogIn;
    AlertDialog.Builder builder;
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        edtEmail=(EditText) findViewById(R.id.edtEmail);
        edtPassword=(EditText) findViewById(R.id.edtPassword);

        txtErrorMsg=(TextView) findViewById(R.id.txtErrorMsg);
        txtErrorEmail=(TextView) findViewById(R.id.txtErrorEmail);

        btnSignUp=(Button) findViewById(R.id.btnSignUp);
        btnLogIn=(Button) findViewById(R.id.btnLogIn);

        edtPassword.addTextChangedListener(this);
        edtEmail.addTextChangedListener(this);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=edtEmail.getText().toString();
                String password=edtPassword.getText().toString();

                String errorEmail=Utils.checkEmail(email);
//                String errorPassword=Utils.checkPasswordComplexity(password);
                if(errorEmail.isEmpty()) {
                    new CheckLogIn().execute(email, password);
                }
                else{
                    txtErrorEmail.setText(errorEmail);
//                    txtErrorMsg.setText(errorPassword);
                }
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        txtErrorMsg.setText("");
        txtErrorEmail.setText("");
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private class CheckLogIn extends AsyncTask<String,Void, RpcLoginUser.LoginUserResponse>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            builder=new AlertDialog.Builder(LogInActivity.this);
            builder.setCancelable(false);

            builder.setView(getLayoutInflater().inflate(R.layout.popup_waiting,null));
            dialog=builder.create();
            dialog.show();
        }

        @Override
        protected RpcLoginUser.LoginUserResponse doInBackground(String... params) {
            String email=params[0];
            String password=params[1];

            ManagedChannel channel=null;
            try{
                channel= ManagedChannelBuilder.forAddress(Config.ip,Config.port)
                        .usePlaintext()
                        .build();

                GatewayGrpc.GatewayBlockingStub blockingStub= GatewayGrpc.newBlockingStub(channel);
                RpcLoginUser.LoginUserRequest request= RpcLoginUser.LoginUserRequest.newBuilder()
                        .setEmail(email)
                        .setPassword(password)
                        .build();
                RpcLoginUser.LoginUserResponse response=blockingStub.loginUser(request);
                return response;

            }
            catch (Exception e){
                Log.e("Error in Login async task:",e.getMessage());
            }
            finally {
                if (channel != null) {
                    channel.shutdown();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(RpcLoginUser.LoginUserResponse response) {
            super.onPostExecute(response);
            dialog.dismiss();
            if(response==null){
                txtErrorMsg.setText("Invalid username or password");
                return ;
            }

            Utils.saveUserInfo(LogInActivity.this,response);

            Intent intent=new Intent(LogInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}