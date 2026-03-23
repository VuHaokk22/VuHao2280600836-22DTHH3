package com.example.bookmanager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime orderDate;
    private double totalAmount;
    private String username;
    private String status; // PENDING, CONFIRMED, SHIPPING, DELIVERED

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> details;

    public Order() {
        this.orderDate = LocalDateTime.now();
        this.status = "PENDING";
    }

    public Long getId() { return id; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public List<OrderDetail> getDetails() { return details; }
    public void setDetails(List<OrderDetail> details) { this.details = details; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStatusLabel() {
        return switch (status) {
            case "PENDING"   -> "Chờ xác nhận";
            case "CONFIRMED" -> "Đã xác nhận";
            case "SHIPPING"  -> "Đang giao hàng";
            case "DELIVERED" -> "Đã giao hàng";
            default -> status;
        };
    }

    public String getStatusColor() {
        return switch (status) {
            case "PENDING"   -> "amber";
            case "CONFIRMED" -> "blue";
            case "SHIPPING"  -> "violet";
            case "DELIVERED" -> "emerald";
            default -> "gray";
        };
    }
}
