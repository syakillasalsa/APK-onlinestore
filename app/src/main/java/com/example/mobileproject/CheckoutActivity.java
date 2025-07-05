package com.example.mobileproject;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.*;

public class CheckoutActivity extends AppCompatActivity {

    private EditText alamatPengiriman, catatanTambahan, editWaktu;
    private RadioGroup radioPembayaran, radioDelivery;
    private RadioButton radioPickup, radioDeliveryOption;
    private TextView txtTotal, txtOngkir;
    private Button btnKonfirmasi;
    private LinearLayout layoutTutorialTransfer;  // layout tutorial transfer

    private int finalTotal = 0;
    private int ongkir = 0;
    private ArrayList<Product> cartItems;

    // Koordinat lokasi toko
    private final double tokoLat = -7.7829;
    private final double tokoLng = 110.4088;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Inisialisasi view
        alamatPengiriman = findViewById(R.id.editAlamat);
        catatanTambahan = findViewById(R.id.editCatatan);
        editWaktu = findViewById(R.id.editTime);
        radioPembayaran = findViewById(R.id.radioPembayaran);
        radioDelivery = findViewById(R.id.radioDelivery);
        radioPickup = findViewById(R.id.radioPickup);
        radioDeliveryOption = findViewById(R.id.radioDeliveryOption);
        txtTotal = findViewById(R.id.txtTotal);
        txtOngkir = findViewById(R.id.txtOngkir);
        btnKonfirmasi = findViewById(R.id.btnKonfirmasi);
        layoutTutorialTransfer = findViewById(R.id.layout_tutorial_transfer);

        editWaktu.setFocusable(false);
        editWaktu.setClickable(true);

        // Pilih waktu
        editWaktu.setOnClickListener(v -> showTimePickerDialog());

        // Ambil item dari intent
        cartItems = (ArrayList<Product>) getIntent().getSerializableExtra("cartItems");
        if (cartItems == null) cartItems = new ArrayList<>();

        for (Product item : cartItems) {
            finalTotal += item.getHargaAsInt() * item.getJumlah();
        }

        txtTotal.setText("Total Harga: Rp" + finalTotal);
        txtOngkir.setText("Ongkir: Rp0");

        // Tampilkan/ Sembunyikan alamat
        radioDelivery.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioDeliveryOption) {
                alamatPengiriman.setVisibility(View.VISIBLE);
            } else {
                alamatPengiriman.setVisibility(View.GONE);
                ongkir = 0;
                txtOngkir.setText("Ongkir: Rp0");
                txtTotal.setText("Total Harga: Rp" + finalTotal);
            }
        });

        // Hitung ongkir saat selesai mengetik alamat
        alamatPengiriman.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && radioDeliveryOption.isChecked()) {
                String alamat = alamatPengiriman.getText().toString().trim();
                if (!alamat.isEmpty()) {
                    hitungOngkirDariAlamat(alamat);
                }
            }
        });

        // Tampilkan atau sembunyikan tutorial transfer berdasarkan pilihan pembayaran
        radioPembayaran.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioTransfer) {
                layoutTutorialTransfer.setVisibility(View.VISIBLE);
            } else {
                layoutTutorialTransfer.setVisibility(View.GONE);
            }
        });

        // Tombol konfirmasi
        btnKonfirmasi.setOnClickListener(v -> {
            String alamat = alamatPengiriman.getText().toString().trim();
            String catatan = catatanTambahan.getText().toString().trim();
            String waktu = editWaktu.getText().toString().trim();

            int selectedPaymentId = radioPembayaran.getCheckedRadioButtonId();
            int selectedDeliveryId = radioDelivery.getCheckedRadioButtonId();

            if (selectedDeliveryId == -1) {
                Toast.makeText(this, "Pilih metode pengiriman", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedDeliveryId == R.id.radioDeliveryOption && alamat.isEmpty()) {
                Toast.makeText(this, "Masukkan alamat pengiriman", Toast.LENGTH_SHORT).show();
                return;
            }

            if (waktu.isEmpty()) {
                Toast.makeText(this, "Masukkan waktu pengiriman", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedPaymentId == -1) {
                Toast.makeText(this, "Pilih metode pembayaran", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedPayment = findViewById(selectedPaymentId);
            String metodePembayaran = selectedPayment.getText().toString();
            String metodePengiriman = (selectedDeliveryId == R.id.radioPickup) ? "Self Pick-Up" : "Delivery";

            // Data transaksi
            Map<String, Object> transaksi = new HashMap<>();
            transaksi.put("alamat", alamat);
            transaksi.put("catatan", catatan);
            transaksi.put("waktu", waktu);
            transaksi.put("metode_pembayaran", metodePembayaran);
            transaksi.put("metode_pengiriman", metodePengiriman);
            transaksi.put("total_harga", finalTotal + ongkir);
            transaksi.put("ongkir", ongkir);

            ArrayList<Map<String, Object>> daftarProduk = new ArrayList<>();
            for (Product item : cartItems) {
                Map<String, Object> produk = new HashMap<>();
                produk.put("nama", item.getNama());
                produk.put("jumlah", item.getJumlah());
                produk.put("harga", item.getHargaAsInt());
                daftarProduk.add(produk);
            }
            transaksi.put("produk", daftarProduk);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("riwayat_transaksi")
                    .add(transaksi)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Pesanan berhasil dikirim dan disimpan", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(CheckoutActivity.this, OrderSummaryActivity.class);
                        intent.putExtra("cartItems", cartItems);
                        intent.putExtra("alamat", alamat);
                        intent.putExtra("catatan", catatan);
                        intent.putExtra("waktu", waktu);
                        intent.putExtra("metode", metodePembayaran);
                        intent.putExtra("deliveryMethod", metodePengiriman);
                        intent.putExtra("total", finalTotal + ongkir);
                        intent.putExtra("ongkir", ongkir);
                        startActivity(intent);

                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Gagal menyimpan transaksi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                TimePickerDialog.THEME_HOLO_LIGHT,
                (view, selectedHour, selectedMinute) -> {
                    String formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    editWaktu.setText(formattedTime);
                },
                hour,
                minute,
                true
        );

        timePickerDialog.show();
    }

    private void hitungOngkirDariAlamat(String alamat) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(alamat, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                double latUser = address.getLatitude();
                double lngUser = address.getLongitude();

                double jarak = calculateDistance(tokoLat, tokoLng, latUser, lngUser);
                ongkir = (int) (jarak * 275); // Ongkir = Rp275/km

                txtOngkir.setText("Ongkir: Rp" + ongkir);
                txtTotal.setText("Total Harga: Rp" + (finalTotal + ongkir));
            } else {
                Toast.makeText(this, "Alamat tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal mengonversi alamat", Toast.LENGTH_SHORT).show();
        }
    }

    // Rumus Haversine
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        int R = 6371; // Radius bumi (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
