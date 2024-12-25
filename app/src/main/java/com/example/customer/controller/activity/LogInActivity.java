package com.example.customer.controller.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.customer.Config.Config;
import com.example.customer.R;
import com.example.customer.utils.PopUpUtils;
import com.example.customer.utils.TextWatcherUtils;
import com.example.customer.utils.Utils;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import vou.proto.GatewayGrpc;
import vou.proto.RpcLoginUser;



public class LogInActivity extends Activity{
    EditText edtEmail,edtPassword;
    TextView txtErrorMsgEmail,txtErrorMsgPassWord,txtErrorMsg;
    Button btnSignUp,btnLogIn;
    AlertDialog.Builder builderWaiting=null;
    AlertDialog dialogWaiting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        edtEmail=(EditText) findViewById(R.id.edtEmail);
        edtPassword=(EditText) findViewById(R.id.edtPassword);

        txtErrorMsg=(TextView) findViewById(R.id.txtErrorMsg);
        txtErrorMsgEmail=(TextView) findViewById(R.id.txtErrorMsgEmail);
        txtErrorMsgPassWord=(TextView) findViewById(R.id.txtErrorMsgPassWord);

        btnSignUp=(Button) findViewById(R.id.btnSignUp);
        btnLogIn=(Button) findViewById(R.id.btnLogIn);

        edtPassword.addTextChangedListener(TextWatcherUtils.createTextWatcher(txtErrorMsgPassWord,txtErrorMsg));
        edtEmail.addTextChangedListener(TextWatcherUtils.createTextWatcher(txtErrorMsgEmail,txtErrorMsg));

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=edtEmail.getText().toString();
                String password=edtPassword.getText().toString();

                String errorEmail=Utils.checkEmail(email);
                String errorPassword=Utils.checkPasswordComplexity(password);
                if(errorEmail.isEmpty()&&errorPassword.isEmpty()) {
                    if(builderWaiting==null){
                        builderWaiting=PopUpUtils.createBuilderWaiting(LogInActivity.this);
                        dialogWaiting=builderWaiting.create();
                    }
                    CheckLogIn checkLogIn=new CheckLogIn();
                    checkLogIn.set_dialogWaiting(dialogWaiting);
                    checkLogIn.execute(email, password);
                }
                else{
                    txtErrorMsgEmail.setText(errorEmail);
                    txtErrorMsgPassWord.setText(errorPassword);
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

    private class CheckLogIn extends AsyncTask<String,Void, RpcLoginUser.LoginUserResponse>{
        private AlertDialog _dialogWaiting;

        public void set_dialogWaiting(AlertDialog _dialogWaiting) {
            this._dialogWaiting = _dialogWaiting;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _dialogWaiting.show();
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
            _dialogWaiting.dismiss();
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