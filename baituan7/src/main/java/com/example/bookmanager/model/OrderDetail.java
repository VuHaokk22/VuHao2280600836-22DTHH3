package com.example.bookmanager.model;

import jakarta.persistence.*;

@Entity
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Long bookId;
    private String bookTitle;
    private double price;
    private int quantity;

    public OrderDetail() {}

    public OrderDetail(Order order, Long bookId, String bookTitle, double price, int quantity) {
        this.order = order;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public Order getOrder() { return order; }
    public Long getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public double getTotal() { return price * quantity; }
}
