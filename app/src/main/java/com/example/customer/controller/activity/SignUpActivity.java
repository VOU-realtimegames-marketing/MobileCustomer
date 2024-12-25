package com.example.customer.controller.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import vou.proto.RpcCreateUser;
import vou.proto.RpcVerifyEmail;

public class SignUpActivity extends Activity {
    EditText edtFullName, edtUserName, edtEmail, edtPassword, edtReTypePassword;
    TextView txtErrorMsgPassword, txtErrorMsgEmail, txtErrorMsgUserName, txtErrorMsgFullName,txtErrorMsg;
    Button btnSignUp, btnLogIn;
    android.app.AlertDialog.Builder builderWaiting=null;
    AlertDialog dialogWaiting=null;
    Handler handler=new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);

        edtFullName = (EditText) findViewById(R.id.edtFullName);
        edtUserName = (EditText) findViewById(R.id.edtUserName);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtReTypePassword = (EditText) findViewById(R.id.edtReTypePassword);

        btnLogIn = (Button) findViewById(R.id.btnLogIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        txtErrorMsgPassword = (TextView) findViewById(R.id.txtErrorMsgPassword);
        txtErrorMsgEmail = (TextView) findViewById(R.id.txtErrorMsgEmail);
        txtErrorMsgUserName = (TextView) findViewById(R.id.txtErrorMsgUserName);
        txtErrorMsgFullName = (TextView) findViewById(R.id.txtErrorMsgFullName);
        txtErrorMsg=(TextView)findViewById(R.id.txtErrorMsg);

        edtFullName.addTextChangedListener(TextWatcherUtils.createTextWatcher(txtErrorMsgFullName,txtErrorMsg));
        edtUserName.addTextChangedListener(TextWatcherUtils.createTextWatcher(txtErrorMsgUserName,txtErrorMsg));
        edtEmail.addTextChangedListener(TextWatcherUtils.createTextWatcher(txtErrorMsgEmail,txtErrorMsg));

        TextWatcher textWatcherPassword=TextWatcherUtils.createTextWatcher(txtErrorMsgPassword,txtErrorMsg);
        edtPassword.addTextChangedListener(textWatcherPassword);
        edtReTypePassword.addTextChangedListener(textWatcherPassword);


        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName=edtFullName.getText().toString();
                String userName=edtUserName.getText().toString();
                String email=edtEmail.getText().toString();
                String password=edtPassword.getText().toString();
                String rePassword=edtReTypePassword.getText().toString();

                String err_fullName= Utils.checkFullName(fullName);
                String err_userName=Utils.checkUserName(userName);
                String err_email=Utils.checkEmail(email);
                String err_password="";
                if(!password.equals(rePassword)){
                    err_password="Retype password does not match";
                }
                else{
                    err_password=Utils.checkPasswordComplexity(password);
                }


                if(err_fullName.isEmpty()&&err_userName.isEmpty()&&err_email.isEmpty()&&err_password.isEmpty()){
                    if(builderWaiting==null){
                        builderWaiting=PopUpUtils.createBuilderWaiting(SignUpActivity.this);
                        dialogWaiting=builderWaiting.create();
                    }
                    CheckSignUp checkSignUp=new CheckSignUp();
                    checkSignUp.set_dialogWaiting(dialogWaiting);
                    checkSignUp.execute(fullName,userName,email,password);
                }
                else{
                    txtErrorMsgFullName.setText(err_fullName);
                    txtErrorMsgUserName.setText(err_userName);
                    txtErrorMsgEmail.setText(err_email);
                    txtErrorMsgPassword.setText(err_password);
                }

            }
        });
    }

    private class CheckSignUp extends AsyncTask<String,Void, RpcCreateUser.CreateUserResponse>{
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
        protected RpcCreateUser.CreateUserResponse doInBackground(String... params) {
            String fullName=params[0];
            String userName=params[1];
            String email=params[2];
            String password=params[3];

            ManagedChannel channel=null;
            try{
                channel= ManagedChannelBuilder.forAddress(Config.ip,Config.port)
                        .usePlaintext()
                        .build();

                GatewayGrpc.GatewayBlockingStub blockingStub= GatewayGrpc.newBlockingStub(channel);
                RpcCreateUser.CreateUserRequest request= RpcCreateUser.CreateUserRequest.newBuilder()
                        .setFullName(fullName)
                        .setUsername(userName)
                        .setEmail(email)
                        .setPassword(password)
                        .build();
                RpcCreateUser.CreateUserResponse response=blockingStub.createUser(request);
                return response;
            }
            catch (Exception e){
                Log.e("Error in signup async task:",e.getMessage());
                handler.post(new Runnable(){
                    @Override
                    public void run() {
                        txtErrorMsg.setText(e.getMessage());
                    }
                });

            }
            finally {
                if (channel != null) {
                    channel.shutdown();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(RpcCreateUser.CreateUserResponse response) {
            super.onPostExecute(response);
            _dialogWaiting.dismiss();
            if(response!=null){
                AlertDialog.Builder builderVerify = new AlertDialog.Builder(SignUpActivity.this);

                builderVerify.setTitle("Verify OTP");

                builderVerify.setMessage("Enter the 6-digit OTP code sent via email");

                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.popup_verify_email, null);
                builderVerify.setView(dialogView);
                EditText edtOTP = dialogView.findViewById(R.id.edtOTP);
                TextView errOTP=dialogView.findViewById(R.id.txtErrorMsg);
                TextView txtVerify=dialogView.findViewById(R.id.txtVerify);
                TextView txtCancel=dialogView.findViewById(R.id.txtCancel);
                edtOTP.addTextChangedListener(TextWatcherUtils.createTextWatcher(errOTP));

                AlertDialog dialogVerify = builderVerify.create();
                dialogVerify.show();

                txtVerify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String otp = edtOTP.getText().toString();
                        Log.e("OTP","OTP"+otp);
                        if (otp.length()<6) {
                            errOTP.setText("OTP must be 6 digits");
                        } else {
                            if(builderWaiting==null){
                                builderWaiting=PopUpUtils.createBuilderWaiting(SignUpActivity.this);
                                dialogWaiting=builderWaiting.create();
                            }
                            VerifyOTP verifyOTP=new VerifyOTP();
                            verifyOTP.setDialogVerify(dialogVerify);
                            verifyOTP.set_dialogWaiting(dialogWaiting);
                            verifyOTP.setErrorOTP(errOTP);
                            verifyOTP.execute(response.getUser().getEmail(),otp);
                        }
                    }
                });

                txtCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogVerify.dismiss();
                    }
                });

            }
//            else{
//                txtErrorMsgPassword.setText("Invalid information");
//            }
        }
    }

    private class VerifyOTP extends AsyncTask<String,Void, RpcVerifyEmail.VerifyEmailResponse>{
        private AlertDialog dialogVerify;
        private AlertDialog _dialogWaiting;
        private TextView errorOTP;

        public void setDialogVerify(AlertDialog dialogVerify) {
            this.dialogVerify = dialogVerify;
        }

        public void set_dialogWaiting(AlertDialog _dialogWaiting) {
            this._dialogWaiting = _dialogWaiting;
        }

        public void setErrorOTP(TextView errorOTP) {
            this.errorOTP = errorOTP;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            _dialogWaiting.show();
        }

        @Override
        protected RpcVerifyEmail.VerifyEmailResponse doInBackground(String... params) {
            String email=params[0];
            String otp=params[1];

            ManagedChannel channel=null;
            try{
                channel=ManagedChannelBuilder.forAddress(Config.ip,Config.port)
                        .usePlaintext()
                        .build();

                GatewayGrpc.GatewayBlockingStub blockingStub=GatewayGrpc.newBlockingStub(channel);
                RpcVerifyEmail.VerifyEmailRequest request= RpcVerifyEmail.VerifyEmailRequest.newBuilder()
                        .setEmail(email)
                        .setSecretCode(otp)
                        .build();

                RpcVerifyEmail.VerifyEmailResponse response=blockingStub.verifyEmail(request);
                return response;
            }
            catch (Exception e){

            }
            finally {
                if (channel != null) {
                    channel.shutdown();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(RpcVerifyEmail.VerifyEmailResponse verifyEmailResponse) {
            super.onPostExecute(verifyEmailResponse);
            dialogWaiting.dismiss();
            if(verifyEmailResponse!=null){
                dialogVerify.dismiss();
                showSuccessDialog();
            }
            else{
                errorOTP.setText("Invalid OTP");
            }
        }
    }


    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign Up Successful").setMessage("What would you like to do next?");

        builder.setPositiveButton("Log In", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}