package com.example.mobileproject;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.*;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OrderSummaryActivity extends AppCompatActivity {

    private TextView txtItems, txtDeliveryMethod, txtAlamat, txtWaktu, txtCatatan, txtPaymentMethod, txtOngkir, txtTotal;
    private TextView txtTanggalWaktuPesan;  // Tambahan TextView untuk tanggal waktu pesan di layar
    private Button btnFinish;

    private ArrayList<Product> cartItems;
    private String alamat, catatan, waktu, metodePembayaran, deliveryMethod;
    private int ongkir;

    private static final int REQUEST_WRITE_PERMISSION = 100;

    // Variabel untuk menyimpan tanggal dan waktu pesanan sekarang
    private String tanggalPesan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_summary_activity);

        // Inisialisasi view
        txtItems = findViewById(R.id.txtItems);
        txtDeliveryMethod = findViewById(R.id.txtDeliveryMethod);
        txtAlamat = findViewById(R.id.txtAlamat);
        txtWaktu = findViewById(R.id.txtWaktu);
        txtCatatan = findViewById(R.id.txtCatatan);
        txtPaymentMethod = findViewById(R.id.txtPaymentMethod);
        txtOngkir = findViewById(R.id.txtOngkir);
        txtTotal = findViewById(R.id.txtTotal);
        btnFinish = findViewById(R.id.btnFinish);

        // **Tambahan: Inisialisasi TextView tanggal waktu pesan di layar**
        txtTanggalWaktuPesan = findViewById(R.id.txtTanggalWaktuPesan);

        // Ambil data dari intent
        cartItems = (ArrayList<Product>) getIntent().getSerializableExtra("cartItems");
        alamat = getIntent().getStringExtra("alamat");
        catatan = getIntent().getStringExtra("catatan");
        waktu = getIntent().getStringExtra("waktu");
        metodePembayaran = getIntent().getStringExtra("metode");
        deliveryMethod = getIntent().getStringExtra("deliveryMethod");
        ongkir = getIntent().getIntExtra("ongkir", 0);

        // Dapatkan tanggal & waktu sekarang sebagai catatan pesanan
        tanggalPesan = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault()).format(new Date());

        int totalHarga = 0;
        StringBuilder itemsBuilder = new StringBuilder();

        for (Product item : cartItems) {
            int hargaSatuan = item.getHargaAsInt();
            int jumlah = item.getJumlah();
            int subtotal = hargaSatuan * jumlah;

            itemsBuilder.append("- ").append(item.getNama())
                    .append(" (x").append(jumlah).append(") = Rp").append(subtotal).append("\n");

            totalHarga += subtotal;
        }

        int totalAkhir = totalHarga + ongkir;

        // Tampilkan data di layar
        txtItems.setText(itemsBuilder.toString());
        txtDeliveryMethod.setText("Metode Pengiriman: " + deliveryMethod);
        txtAlamat.setText(deliveryMethod.equals("Delivery") ? "Alamat: " + alamat : "");
        txtWaktu.setText("Waktu: " + waktu);
        txtCatatan.setText("Catatan: " + (catatan.isEmpty() ? "-" : catatan));
        txtPaymentMethod.setText("Metode Pembayaran: " + metodePembayaran);
        txtOngkir.setText("Ongkir: Rp" + ongkir);
        txtTotal.setText("Total: Rp" + totalAkhir);

        // **Tampilkan tanggal & waktu pesan di layar order summary**
        txtTanggalWaktuPesan.setText("Tanggal & Waktu Pesan: " + tanggalPesan);

        btnFinish.setOnClickListener(v -> {
            // Simpan nota otomatis saat tombol ditekan
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                savePdfToDownloadQAndAbove(itemsBuilder.toString(), true);
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
                } else {
                    savePdfToDownloadBelowQ(itemsBuilder.toString(), true);
                }
            }

            // Setelah proses simpan PDF, pindah ke HomeActivity
            Intent intent = new Intent(OrderSummaryActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void savePdfToDownloadQAndAbove(String items, boolean showToast) {
        PdfDocument document = generatePdf(items);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "Nota_Pesanan_" + timeStamp + ".pdf";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
        values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            document.writeTo(outputStream);
            document.close();
            outputStream.close();
            if (showToast) {
                Toast.makeText(this, "Nota berhasil disimpan di folder Download", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (showToast) {
                Toast.makeText(this, "Gagal menyimpan PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePdfToDownloadBelowQ(String items, boolean showToast) {
        PdfDocument document = generatePdf(items);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "Nota_Pesanan_" + timeStamp + ".pdf";

        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        java.io.File file = new java.io.File(directoryPath, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            if (showToast) {
                Toast.makeText(this, "Nota berhasil disimpan di Download", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (showToast) {
                Toast.makeText(this, "Gagal menyimpan PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private PdfDocument generatePdf(String items) {
        PdfDocument document = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int y = 50;
        paint.setTextSize(18);
        paint.setFakeBoldText(true);
        canvas.drawText("NOTA PESANAN", 220, y, paint);

        y += 30;
        paint.setTextSize(12);
        paint.setFakeBoldText(false);
        // Tampilkan tanggal pesan di PDF
        canvas.drawText("Tanggal Pesan: " + tanggalPesan, 40, y, paint);

        y += 30;
        paint.setTextSize(14);
        paint.setFakeBoldText(false);

        canvas.drawText("Daftar Item:", 40, y, paint);
        y += 20;
        for (String line : items.split("\n")) {
            canvas.drawText(line, 60, y, paint);
            y += 20;
        }

        y += 10;
        canvas.drawText(txtDeliveryMethod.getText().toString(), 40, y, paint);
        y += 20;

        if (!txtAlamat.getText().toString().isEmpty()) {
            canvas.drawText(txtAlamat.getText().toString(), 40, y, paint);
            y += 20;
        }

        canvas.drawText(txtWaktu.getText().toString(), 40, y, paint);
        y += 20;
        canvas.drawText(txtCatatan.getText().toString(), 40, y, paint);
        y += 20;
        canvas.drawText(txtPaymentMethod.getText().toString(), 40, y, paint);
        y += 20;
        canvas.drawText(txtOngkir.getText().toString(), 40, y, paint);
        y += 20;
        canvas.drawText(txtTotal.getText().toString(), 40, y, paint);
        y += 20;

        document.finishPage(page);
        return document;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                savePdfToDownloadBelowQ(txtItems.getText().toString(), true);
            } else {
                Toast.makeText(this, "Izin ditolak untuk menyimpan file", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
