package com.example.customer.controller.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.customer.Config.Config;
import com.example.customer.R;
import com.example.customer.data.Event;
import com.example.customer.data.Game;
import com.example.customer.data.Voucher;
import com.example.customer.utils.Utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import vou.proto.EventServiceGrpc;
import vou.proto.GatewayGrpc;
import vou.proto.RpcGetMyVouchers;

public class FragmentVoucher extends Fragment {

    private List<Voucher> vouchers = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String accessToken = Utils.getAccessToken(requireActivity());
        GetMyVouchers getMyVouchers = new GetMyVouchers(accessToken);
        getMyVouchers.execute();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voucher, container, false);
        ListView listView = view.findViewById(R.id.list_vouchers);
        VoucherAdapter adapter = new VoucherAdapter(getContext(), vouchers);
        listView.setAdapter(adapter);
        return view;
    }

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

    private class GetMyVouchers extends AsyncTask<Void, Void, RpcGetMyVouchers.GetMyVouchersResponse> {

        private String accessToken;

        public GetMyVouchers(String accessToken) {
            this.accessToken = accessToken;
        }

        @Override
        protected RpcGetMyVouchers.GetMyVouchersResponse doInBackground(Void... voids) {

            ManagedChannel channel = null;
            try {
                channel = ManagedChannelBuilder.forAddress(Config.ip,Config.event_port)
                        .usePlaintext()
                        .intercept(new AuthInterceptor(accessToken))
                        .build();

                GatewayGrpc.GatewayBlockingStub blockingStub = GatewayGrpc.newBlockingStub(channel);
                RpcGetMyVouchers.GetMyVouchersRequest request = RpcGetMyVouchers.GetMyVouchersRequest.newBuilder()
                        .build();

                RpcGetMyVouchers.GetMyVouchersResponse response = blockingStub.getMyVouchers(request);
                return response;
            }
            catch (Exception e) {
                Log.e("Error in Get My Vouchers async task:", e.getMessage());
            }
            finally {
                if (channel != null) {
                    channel.shutdown();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(RpcGetMyVouchers.GetMyVouchersResponse response) {
            super.onPostExecute(response);
            if (response != null) {
                List<Voucher> convertedVouchers = new ArrayList<>();
                for (vou.proto.VoucherOuterClass.Voucher grpcVoucher : response.getVouchersList()) {
                    Voucher voucher = new Voucher(
                            grpcVoucher.getId(),
                            grpcVoucher.getEventId(),
                            grpcVoucher.getQrCode(),
                            grpcVoucher.getType(),
                            grpcVoucher.getStatus(),
                            grpcVoucher.getDiscount(),
                            convertToLocalDateTime(grpcVoucher.getExpiresAt().getSeconds())
                    );
                    convertedVouchers.add(voucher);
                }
                vouchers.clear();
                vouchers.addAll(convertedVouchers);

                FragmentVoucher.this.getActivity().runOnUiThread(() -> {
                    ListView listView = getView().findViewById(R.id.list_vouchers);
                    if (listView != null) {
                        listView.invalidateViews();
                    }
                });

            }
        }

        private LocalDateTime convertToLocalDateTime(long seconds) {
            return LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.UTC);
        }


    }

}
class VoucherAdapter extends ArrayAdapter<Voucher> {
    private Context context;
    private List<Voucher> vouchers;

    public VoucherAdapter(@NonNull Context context, @NonNull List<Voucher> vouchers) {
        super(context, 0, vouchers);
        this.context = context;
        this.vouchers = vouchers;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_voucher, parent, false);
        }

        Voucher voucher = vouchers.get(position);

        TextView name = convertView.findViewById(R.id.event_name);
        TextView expiresAt = convertView.findViewById(R.id.expires_at);
        TextView voucherQuantity = convertView.findViewById(R.id.voucher_quantity);



        expiresAt.setText("Expires at: " + voucher.getExpiresAt());
        voucherQuantity.setText(" - " + String.valueOf(voucher.getDiscount()) + " %");

        return convertView;
    }
}
