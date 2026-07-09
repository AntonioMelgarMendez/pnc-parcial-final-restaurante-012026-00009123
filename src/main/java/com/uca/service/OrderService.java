package com.uca.service;

import com.uca.entity.Order;
import com.uca.entity.Role;
import com.uca.entity.User;
import com.uca.repository.OrderRepository;
import com.uca.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        User user = SecurityUtils.getAuthenticatedUser();
        if (user.getRole() == Role.ADMIN) {
            return orderRepository.findAll();
        } else if (user.getRole() == Role.MANAGER) {
            return orderRepository.findByBranchId(user.getBranch().getId());
        } else {
            return orderRepository.findByCustomerId(user.getId());
        }
    }

    public Order getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow();
        validateAccess(order);
        return order;
    }

    public Order createOrder(Order order) {
        User user = SecurityUtils.getAuthenticatedUser();
        if (user.getRole() == Role.CUSTOMER) {
            order.setCustomer(user);
        }
        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, Order orderDetails) {
        Order order = orderRepository.findById(id).orElseThrow();
        validateAccess(order); // This enforces Option B
        
        // Update logic...
        order.setStatus(orderDetails.getStatus());
        // For simplicity, just updating status here
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow();
        validateAccess(order); // This enforces Option B
        orderRepository.delete(order);
    }

    private void validateAccess(Order order) {
        User user = SecurityUtils.getAuthenticatedUser();
        
        if (user.getRole() == Role.ADMIN) {
            return; // Admin can access anything
        }
        
        if (user.getRole() == Role.MANAGER) {
            if (user.getBranch() == null || !user.getBranch().getId().equals(order.getBranch().getId())) {
                throw new AccessDeniedException("Manager can only manage orders of their own branch.");
            }
        } else if (user.getRole() == Role.CUSTOMER) {
            if (!user.getId().equals(order.getCustomer().getId())) {
                throw new AccessDeniedException("Customers can only view/modify their own orders.");
            }
        }
    }
}
