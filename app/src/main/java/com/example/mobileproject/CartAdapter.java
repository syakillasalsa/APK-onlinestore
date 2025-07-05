package com.example.mobileproject;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CartAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Product> cartItems;
    private TextView totalHargaTextView;

    public CartAdapter(Context context, ArrayList<Product> cartItems, TextView totalHargaTextView) {
        this.context = context;
        this.cartItems = cartItems;
        this.totalHargaTextView = totalHargaTextView;
        updateTotal(); // Inisialisasi total saat adapter dibuat
    }

    private void updateTotal() {
        int total = 0;
        for (Product item : cartItems) {
            String hargaStr = item.getHarga().replace("Rp ", "").replace(".", "").replace(",", "");
            try {
                int harga = Integer.parseInt(hargaStr);
                total += harga * item.getJumlah();
            } catch (NumberFormatException e) {
                e.printStackTrace(); // Debug error parsing
            }
        }
        totalHargaTextView.setText("Total: Rp " + total);
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int i) {
        return cartItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Product item = cartItems.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        }

        ImageView imgProduct = convertView.findViewById(R.id.imgProduct);
        TextView txtNama = convertView.findViewById(R.id.txtNama);
        TextView txtHarga = convertView.findViewById(R.id.txtHarga);
        TextView txtJumlah = convertView.findViewById(R.id.txtJumlah);
        Button btnPlus = convertView.findViewById(R.id.btnPlus);
        Button btnMinus = convertView.findViewById(R.id.btnMinus);

        txtNama.setText(item.getNama());
        txtHarga.setText(item.getHarga());
        txtJumlah.setText(String.valueOf(item.getJumlah()));

        Glide.with(context).load(item.getGambarUrl()).into(imgProduct);

        btnPlus.setOnClickListener(v -> {
            item.setJumlah(item.getJumlah() + 1);
            txtJumlah.setText(String.valueOf(item.getJumlah()));
            updateTotal();
            notifyDataSetChanged();
        });

        btnMinus.setOnClickListener(v -> {
            if (item.getJumlah() > 1) {
                item.setJumlah(item.getJumlah() - 1);
                txtJumlah.setText(String.valueOf(item.getJumlah()));
                updateTotal();
                notifyDataSetChanged();
            } else {
                // Konfirmasi hapus
                new AlertDialog.Builder(context)
                        .setTitle("Hapus Barang")
                        .setMessage("Apakah Anda ingin menghapus barang ini dari keranjang?")
                        .setPositiveButton("Ya", (dialog, which) -> {
                            cartItems.remove(item);
                            updateTotal();
                            notifyDataSetChanged();
                        })
                        .setNegativeButton("Tidak", null)
                        .show();
            }
        });

        return convertView;
    }
}
