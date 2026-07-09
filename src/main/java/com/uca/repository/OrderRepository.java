package com.uca.repository;

import com.uca.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBranchId(Long branchId);
    List<Order> findByCustomerId(Long customerId);
}
