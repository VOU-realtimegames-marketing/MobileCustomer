package com.example.customer.controller.fragment;

import android.content.Context;
import android.os.Bundle;
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

import com.example.customer.R;
import com.example.customer.data.Friend;

import java.util.ArrayList;
import java.util.List;

public class FragmentFriend extends Fragment {
    List<Friend> friends = new ArrayList<>();

    Friend friend1 = new Friend(1, "vuong", "Tran Vuong", "vuong@gmail.com", "0987654321", R.drawable.default_avatar, "user", "ask_playturn");
    Friend friend2 = new Friend(2, "phong", "Cao Phong", "vuong@gmail.com", "0987654321", R.drawable.default_avatar, "user", "");
    Friend friend3 = new Friend(3, "cuong", "Huynh Cuong", "vuong@gmail.com", "0987654321", R.drawable.default_avatar, "user", "");
    Friend friend4 = new Friend(4, "hung", "Nguyen Hung", "vuong@gmail.com", "0987654321", R.drawable.default_avatar, "user", "ask_playturn");
    Friend friend5 = new Friend(5, "lam", "Nguyen Lam", "vuong@gmail.com", "0987654321", R.drawable.default_avatar, "user", "");

    Friend[] friendsArray = {friend1, friend2, friend3, friend4, friend5};


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friends.addAll(List.of(friendsArray));
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
        TextView status = convertView.findViewById(R.id.status);


        imageView.setImageResource(friend.getAvatar());
        name.setText(friend.getFullName());
        if (friend.getStatus().equals("ask_playturn")){
            status.setText("Asking for Playturn");
        }
        else{
            status.setText(friend.getStatus());
        }

        return convertView;
    }
}