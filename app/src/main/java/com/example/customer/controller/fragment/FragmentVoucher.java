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
import com.example.customer.data.Event;
import com.example.customer.data.Game;
import com.example.customer.data.Voucher;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FragmentVoucher extends Fragment {

    private List<Voucher> vouchers = new ArrayList<>();
    Voucher voucher1 = new Voucher(1, 1, "Event 1", "", "", LocalDateTime.now(), 10);
    Voucher voucher2 = new Voucher(2, 1, "Event 1", "", "", LocalDateTime.now(), 10);
    Voucher voucher3 = new Voucher(3, 2, "Event 2", "", "", LocalDateTime.now(), 30);
    Voucher voucher4 = new Voucher(4, 3, "Event 3", "", "", LocalDateTime.now(), 20);
    Voucher voucher5 = new Voucher(5, 4, "Event 4", "", "", LocalDateTime.now(), 10);

    Voucher[] list_vouchers = {voucher1, voucher2, voucher3, voucher4, voucher5};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vouchers.addAll(List.of(list_vouchers));
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


        name.setText(voucher.getEventName());
        expiresAt.setText("Expires at: " + voucher.getExpiresAt());
        voucherQuantity.setText(" - " + String.valueOf(voucher.getVoucherQuantity()) + " %");

        return convertView;
    }
}
