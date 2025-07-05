package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MenuFragment extends Fragment {

    private ListView listView;
    private EditText searchBar;
    private ArrayList<Product> productList;
    private ProductAdapter adapter;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        listView = view.findViewById(R.id.productList);
        searchBar = view.findViewById(R.id.search_bar); // dari fragment layout

        productList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        boolean isLoggedIn = mAuth.getCurrentUser() != null;

        adapter = new ProductAdapter(requireContext(), productList, isLoggedIn);
        listView.setAdapter(adapter);

        loadProductsFromFirestore();

        // Event pencarian
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
        });

        return view;
    }

    private void loadProductsFromFirestore() {
        db.collection("produk")
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    productList.clear();
                    for (DocumentSnapshot doc : querySnapshots) {
                        String nama = doc.getString("nama");
                        Long harga = doc.getLong("harga");
                        String gambar = doc.getString("gambar");

                        if (nama != null && harga != null && gambar != null) {
                            productList.add(new Product(nama, "Rp " + harga, gambar));
                        }
                    }
                    adapter.updateList(productList);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Gagal memuat produk", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error", e);
                });
    }
}
