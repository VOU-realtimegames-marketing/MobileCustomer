package com.example.customer.utils;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

public class AuthInterceptor implements ClientInterceptor {
    private final String bearerToken;

    public AuthInterceptor(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                Metadata.Key<String> AUTHORIZATION_KEY = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
                headers.put(AUTHORIZATION_KEY, "Bearer " + bearerToken);
                super.start(responseListener, headers);
            }
        };
    }
}
