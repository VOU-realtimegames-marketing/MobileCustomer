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
import com.example.customer.data.Friend;
import com.example.customer.utils.AuthInterceptor;
import com.example.customer.utils.Utils;


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
import vou.proto.GatewayGrpc;
import vou.proto.RpcGetAllOtherUsers;

public class FragmentFriend extends Fragment {
    List<Friend> friends = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String accessToken = Utils.getAccessToken(requireActivity());
        GetAllOtherUsers getAllOtherUsers = new GetAllOtherUsers(accessToken);
        getAllOtherUsers.execute();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend,null);

        ListView listView = view.findViewById(R.id.list_friends);
        FriendAdapter adapter = new FriendAdapter(getContext(), friends);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Friend clickedFriend = adapter.getItem(position);

            if (clickedFriend != null) {
                FragmentFriendDetail detailFragment = new FragmentFriendDetail();
                Bundle args = new Bundle();
                args.putSerializable("friend", clickedFriend);
                detailFragment.setArguments(args);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack("FragmentFriendDetail")
                        .commit();

            }
        });

        return view;
    }



    private class GetAllOtherUsers extends AsyncTask<Void, Void, RpcGetAllOtherUsers.GetAllOtherUsersResponse> {
        private String accessToken;

        public GetAllOtherUsers(String accessToken) {
            this.accessToken = accessToken;
        }

        @Override
        protected RpcGetAllOtherUsers.GetAllOtherUsersResponse doInBackground(Void... voids) {
            ManagedChannel channel = null;
            try {
                channel = ManagedChannelBuilder.forAddress(Config.ip,Config.port)
                        .usePlaintext()
                        .intercept(new AuthInterceptor(accessToken))
                        .build();

                GatewayGrpc.GatewayBlockingStub blockingStub = GatewayGrpc.newBlockingStub(channel);
                RpcGetAllOtherUsers.GetAllOtherUsersRequest request = RpcGetAllOtherUsers.GetAllOtherUsersRequest.newBuilder()
                        .build();
                RpcGetAllOtherUsers.GetAllOtherUsersResponse response = blockingStub.getAllOtherUsers(request);

                return response;
            }
            catch (Exception e) {
                Log.e("Error in GetAllOtherUsers", e.getMessage());
            }
            finally {
                if (channel != null) {
                    channel.shutdown();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(RpcGetAllOtherUsers.GetAllOtherUsersResponse response) {
            super.onPostExecute(response);
            if (response != null) {
                List<Friend> convertedFriends = new ArrayList<>();
                for (vou.proto.UserOuterClass.User grpcUser : response.getUsersList()) {
                    Friend friend = new Friend(
                            grpcUser.getUsername(),
                            grpcUser.getFullName(),
                            grpcUser.getRole()
                    );
                    convertedFriends.add(friend);
                }
                friends.clear();
                friends.addAll(convertedFriends);

                FragmentFriend.this.getActivity().runOnUiThread(() -> {
                    ListView listView = getView().findViewById(R.id.list_friends);
                    if (listView != null) {
                        listView.invalidateViews();
                    }
                });

            }
        }

    }



}
class FriendAdapter extends ArrayAdapter<Friend> {
    private Context context;
    private List<Friend> friends;

    public FriendAdapter(@NonNull Context context, @NonNull List<Friend> friends) {
        super(context, 0, friends);
        this.context = context;
        this.friends = friends;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        }

        Friend friend = friends.get(position);

        ImageView imageView = convertView.findViewById(R.id.friend_image);
        TextView name = convertView.findViewById(R.id.friend_name);
        TextView role = convertView.findViewById(R.id.status);


        imageView.setImageResource(R.drawable.default_avatar);
        name.setText(friend.getFullName());
        role.setText("Role: " + friend.getRole());


        return convertView;
    }
}