package com.example.bookmanager.controller;

import com.example.bookmanager.model.CartItem;
import com.example.bookmanager.model.Order;
import com.example.bookmanager.model.OrderDetail;
import com.example.bookmanager.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if (cart == null) cart = new LinkedHashMap<>();
        double grandTotal = cart.values().stream().mapToDouble(CartItem::getTotal).sum();
        model.addAttribute("cartItems", cart.values());
        model.addAttribute("grandTotal", grandTotal);
        return "cart";
    }

    @PostMapping("/remove/{id}")
    public String removeItem(@PathVariable Long id, HttpSession session) {
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if (cart != null) cart.remove(id);
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session,
                           @AuthenticationPrincipal UserDetails userDetails) {
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return "redirect:/cart";

        Order order = new Order();
        order.setUsername(userDetails.getUsername());

        List<OrderDetail> details = new ArrayList<>();
        double total = 0;
        for (CartItem item : cart.values()) {
            details.add(new OrderDetail(order, item.getBookId(), item.getTitle(), item.getPrice(), item.getQuantity()));
            total += item.getTotal();
        }
        order.setTotalAmount(total);
        order.setDetails(details);
        orderRepository.save(order);

        session.removeAttribute("cart");
        // Redirect về trang sản phẩm sau khi đặt hàng thành công
        return "redirect:/books?ordered=true";
    }

    @GetMapping("/orders")
    public String myOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        List<Order> orders = orderRepository.findByUsernameOrderByOrderDateDesc(userDetails.getUsername());
        model.addAttribute("orders", orders);
        return "my-orders";
    }
}
