package com.example.mobileproject;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private String nama;
    private String harga;
    private String gambarUrl;
    private int jumlah;
    private int imageResId;

    public Product() {}

    public Product(String nama, String harga, String gambarUrl) {
        this.nama = nama;
        this.harga = harga;
        this.gambarUrl = gambarUrl;
        this.imageResId = 0;
        this.jumlah = 1;
    }

    public Product(String nama, String harga, String gambarUrl, int imageResId, int jumlah) {
        this.nama = nama;
        this.harga = harga;
        this.gambarUrl = gambarUrl;
        this.imageResId = imageResId;
        this.jumlah = jumlah;
    }

    protected Product(Parcel in) {
        nama = in.readString();
        harga = in.readString();
        gambarUrl = in.readString();
        jumlah = in.readInt();
        imageResId = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getHarga() { return harga; }
    public void setHarga(String harga) { this.harga = harga; }

    public String getGambarUrl() { return gambarUrl; }
    public void setGambarUrl(String gambarUrl) { this.gambarUrl = gambarUrl; }

    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public int getHargaAsInt() {
        try {
            return Integer.parseInt(harga.replace("Rp", "").replace(".", "").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nama);
        dest.writeString(harga);
        dest.writeString(gambarUrl);
        dest.writeInt(jumlah);
        dest.writeInt(imageResId);
    }
}