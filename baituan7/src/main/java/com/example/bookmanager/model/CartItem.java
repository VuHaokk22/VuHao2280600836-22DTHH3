package com.example.bookmanager.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private Long bookId;
    private String title;
    private double price;
    private int quantity;

    public CartItem(Long bookId, String title, double price, int quantity) {
        this.bookId = bookId;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getBookId() { return bookId; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getTotal() { return price * quantity; }
}
