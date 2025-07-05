package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    private LinearLayout rekomendasiContainer;
    private FirebaseFirestore db;

    private static final double LAT = -7.639522521086941;
    private static final double LNG = 110.89682153791783;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();
        rekomendasiContainer = root.findViewById(R.id.rekomendasi_container);

        //
        // Text Hello User
        TextView haloUserText = root.findViewById(R.id.halo_user);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            if (email != null && email.contains("@")) {
                String nama = email.substring(0, email.indexOf('@'));
                haloUserText.setText("Hello, " + nama);
            } else {
                haloUserText.setText("Hello, Guest");
            }
        } else {
            haloUserText.setText("Hello, Guest");
        }

        // Gambar Map Screenshot dan sebagai tombol ke MapsActivity
        ImageView mapImage = root.findViewById(R.id.static_map_image);
        mapImage.setImageResource(R.drawable.qwe); // pastikan map_screenshot.jpg/png ada di res/drawable

        mapImage.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            startActivity(intent);
        });

        // Load produk rekomendasi
        loadRekomendasiItems();

        return root;
    }

    private void loadRekomendasiItems() {
        db.collection("produk")
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    rekomendasiContainer.removeAllViews();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String nama = doc.getString("nama");
                        Long harga = doc.getLong("harga");
                        String gambarUrl = doc.getString("gambar");

                        if (nama != null && harga != null && gambarUrl != null) {
                            View itemView = LayoutInflater.from(getContext())
                                    .inflate(R.layout.item_rekomendasi, rekomendasiContainer, false);

                            TextView namaText = itemView.findViewById(R.id.txtNamaProduk);
                            ImageView gambarView = itemView.findViewById(R.id.imgProduk);

                            namaText.setText(nama);
                            Glide.with(getContext()).load(gambarUrl).into(gambarView);

                            rekomendasiContainer.addView(itemView);
                        }
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }
}
