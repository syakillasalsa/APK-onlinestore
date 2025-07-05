package com.example.mobileproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CheckoutAdapter extends ArrayAdapter<Product> {
    public CheckoutAdapter(Context context, ArrayList<Product> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Product item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        TextView line1 = convertView.findViewById(android.R.id.text1);
        TextView line2 = convertView.findViewById(android.R.id.text2);

        line1.setText(item.getNama());
        line2.setText(item.getHarga());

        return convertView;
    }
}