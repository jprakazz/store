package com.example.store.repository;

import com.example.store.entity.Order;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @EntityGraph(attributePaths = {"customer", "products"})
    @Override
    List<Order> findAll();
    
    @EntityGraph(attributePaths = {"customer", "products"})
    @Override
    Optional<Order> findById(Long id);
    
    @EntityGraph(attributePaths = {"customer", "products"})
    @Query("SELECT o FROM Order o JOIN o.products p WHERE p.id = :productId")
    List<Order> findOrdersByProductId(@Param("productId") Long productId);
}
