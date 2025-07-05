package com.example.mobileproject;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    ListView cartListView;
    ArrayList<Product> cartItems;
    ProductAdapter adapter;
    TextView totalHarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_cart);

        cartListView = findViewById(R.id.cartList);
        totalHarga = findViewById(R.id.totalHarga);

        cartItems = Cart.getItems();
        adapter = new ProductAdapter(this, cartItems, true);
        cartListView.setAdapter(adapter);

        hitungTotal();
    }

    private void hitungTotal() {
        int total = 0;
        for (Product item : cartItems) {
            String hargaStr = item.getHarga().replace("Rp ", "").replace(".", "");
            total += Integer.parseInt(hargaStr);
        }
        totalHarga.setText("Total: Rp " + total);
    }
}