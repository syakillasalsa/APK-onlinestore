package com.example.mobileproject;

import java.util.ArrayList;

public class Cart {
    private static ArrayList<Product> cartItems = new ArrayList<>();

    public static void addItem(Product newItem) {
        for (Product item : cartItems) {
            if (item.getNama().equals(newItem.getNama())) {
                item.setJumlah(item.getJumlah() + newItem.getJumlah());
                return;
            }
        }
        cartItems.add(newItem);
    }

    public static ArrayList<Product> getItems() {
        return cartItems;
    }

    public static void clear() {
        cartItems.clear();
    }
}