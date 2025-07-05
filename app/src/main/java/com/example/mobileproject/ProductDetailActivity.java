package com.example.mobileproject;



import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imgDetail;
    private TextView txtDetailNama, txtDetailHarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        imgDetail = findViewById(R.id.imgDetail);
        txtDetailNama = findViewById(R.id.txtDetailNama);
        txtDetailHarga = findViewById(R.id.txtDetailHarga);

        // Ambil data dari Intent
        String nama = getIntent().getStringExtra("nama");
        String harga = getIntent().getStringExtra("harga");
        String gambar = getIntent().getStringExtra("gambar");

        txtDetailNama.setText(nama);
        txtDetailHarga.setText(harga);

        Glide.with(this)
                .load(gambar)
                .into(imgDetail);
    }
}

