package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class CartFragment extends Fragment {

    private ListView cartListView;
    private TextView totalHarga;
    private Button btnCheckout;

    private ArrayList<Product> cartItems;
    private CartAdapter adapter;

    public CartFragment() {
        // Diperlukan konstruktor kosong
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartListView = view.findViewById(R.id.cartList);
        totalHarga = view.findViewById(R.id.totalHarga);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        // Ambil item dari keranjang
        cartItems = Cart.getItems();

        // Set adapter
        adapter = new CartAdapter(requireContext(), cartItems, totalHarga);
        cartListView.setAdapter(adapter);

        updateTotalHarga();

        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(getContext(), "Keranjang kosong!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kirim ke CheckoutActivity
            Intent intent = new Intent(requireContext(), CheckoutActivity.class);
            intent.putParcelableArrayListExtra("cartItems", cartItems);
            startActivity(intent);
        });

        return view;
    }

    private void updateTotalHarga() {
        int total = 0;
        for (Product item : cartItems) {
            total += item.getHargaAsInt() * item.getJumlah();
        }
        totalHarga.setText("Total: Rp" + total);
    }
}
