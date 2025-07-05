package com.example.mobileproject;

public class CartItem {
    private String name;
    private int quantity;
    private int price;
    private int imageResId;

    public CartItem(String name, int quantity, int price, int imageResId) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public int getPrice() { return price; }
    public int getImageResId() { return imageResId; }
}