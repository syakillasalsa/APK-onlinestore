package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private ImageView imgHome, imgCart, imgMenu;
    private Button btnLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bottom_menu);

        imgHome = findViewById(R.id.imgHome);
        imgCart = findViewById(R.id.imgCart);
        imgMenu = findViewById(R.id.imgMenu);
        btnLogin = findViewById(R.id.btnLogin);

        mAuth = FirebaseAuth.getInstance();

        // Tampilkan fragment default
        loadFragment(new HomeFragment());

        imgHome.setOnClickListener(v -> loadFragment(new HomeFragment()));
        imgCart.setOnClickListener(v -> loadFragment(new CartFragment()));
        imgMenu.setOnClickListener(v -> loadFragment(new MenuFragment()));

        // Set tombol Login/Logout sesuai status
        updateLoginButton();

        btnLogin.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                // Jika sudah login, maka logout
                mAuth.signOut();
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish();
            } else {
                // Jika belum login, pindah ke MainActivity untuk login
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void updateLoginButton() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            btnLogin.setText("Logout");
        } else {
            btnLogin.setText("Login");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLoginButton(); // agar teks tombol diperbarui saat kembali ke HomeActivity
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
