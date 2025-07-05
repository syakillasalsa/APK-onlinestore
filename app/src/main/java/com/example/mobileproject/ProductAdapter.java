package com.example.mobileproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {

    private final Context context;
    private final boolean isLoggedIn;
    private ArrayList<Product> originalList;
    private ArrayList<Product> filteredList;

    public ProductAdapter(Context context, ArrayList<Product> productList, boolean isLoggedIn) {
        super(context, 0, productList);
        this.context = context;
        this.isLoggedIn = isLoggedIn;
        this.originalList = new ArrayList<>(productList);
        this.filteredList = productList;
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Product getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Product product = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_item, parent, false);
        }

        ImageView imgProduct = convertView.findViewById(R.id.imgProduct);
        TextView txtNama = convertView.findViewById(R.id.txtNama);
        TextView txtHarga = convertView.findViewById(R.id.txtHarga);
        ImageButton btnTambah = convertView.findViewById(R.id.btnTambah);

        txtNama.setText(product.getNama());
        txtHarga.setText(product.getHarga());

        Glide.with(getContext())
                .load(product.getGambarUrl())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imgProduct);
//harus login sebelum masuk ke keranjang
        btnTambah.setOnClickListener(v -> {
            if (!isLoggedIn) {
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                Toast.makeText(context, "Silakan login terlebih dahulu untuk menambahkan ke keranjang", Toast.LENGTH_SHORT).show();
            } else {
                Cart.addItem(product);
                Toast.makeText(context, product.getNama() + " berhasil ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
            }
        });

        // Klik ke detail produk
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("nama", product.getNama());
            intent.putExtra("harga", product.getHarga());
            intent.putExtra("gambar", product.getGambarUrl());
            context.startActivity(intent);
        });

        return convertView;
    }

    public void updateList(ArrayList<Product> newList) {
        this.originalList = new ArrayList<>(newList);
        this.filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void filter(String keyword) {
        filteredList.clear();
        if (keyword.isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            String lowerKeyword = keyword.toLowerCase();
            for (Product product : originalList) {
                if (product.getNama().toLowerCase().contains(lowerKeyword)) {
                    filteredList.add(product);
                }
            }
        }
        notifyDataSetChanged();
    }
}
