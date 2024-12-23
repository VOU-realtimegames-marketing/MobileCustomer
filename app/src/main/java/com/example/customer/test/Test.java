package com.example.customer.test;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.example.customer.R;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import vou.proto.GatewayGrpc;
import vou.proto.RpcLoginUser;

public class Test extends Activity {
    TextView txtMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        txtMsg=(TextView) findViewById(R.id.txtMsg);
        new BackGroundTask().execute();

    }

    private class BackGroundTask extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... voids) {
            String res="";
            ManagedChannel channel=null;
            try{
                channel= ManagedChannelBuilder.forAddress("10.0.231.254",9090)
                        .usePlaintext()
                        .build();

                GatewayGrpc.GatewayBlockingStub blockingStub=GatewayGrpc.newBlockingStub(channel);
                RpcLoginUser.LoginUserRequest request= RpcLoginUser.LoginUserRequest.newBuilder()
                        .setEmail("admin12345@gmail.com")
                        .setPassword("password123456789")
                        .build();

                RpcLoginUser.LoginUserResponse response=blockingStub.loginUser(request);
                res+="username: "+response.getUser().getUsername()+"\n";
                res+="session_id: "+response.getSessionId()+"\n";
                res+="access_token: "+response.getAccessToken()+"\n";
                res+="refresh_token: "+response.getRefreshToken()+"\n";
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
            finally {
                if (channel != null) {
                    channel.shutdown();
                }
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            txtMsg.setText(s);
        }
    }
}