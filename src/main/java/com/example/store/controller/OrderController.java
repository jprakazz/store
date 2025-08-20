package com.example.store.controller;

import com.example.store.dto.OrderDTO;

import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.CustomerRepository;
import com.example.store.repository.OrderRepository;
import com.example.store.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderController {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @GetMapping
    @Cacheable("orders")
    public List<OrderDTO> getAllOrders() {
        return orderMapper.ordersToOrderDTOs(orderRepository.findAll());
    }

    @GetMapping("/{id}")
    @Cacheable(value = "order", key = "#id")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            return ResponseEntity.ok(orderMapper.orderToOrderDTO(order.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public OrderDTO createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        // Validate customer exists
        Customer customer = customerRepository.findById(orderDTO.getCustomer().getId())
            .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        
        // Validate all products exist
        Set<Long> productIds = new HashSet<>(orderDTO.getProductIds());
        
        List<Product> products = productRepository.findAllById(productIds);
        if (products.size() != productIds.size()) {
            throw new EntityNotFoundException("One or more products not found");
        }
        
        // Convert DTO to Entity and save
        Order order = orderMapper.orderDTOToOrder(orderDTO);
        order.setCustomer(customer);
        order.setProducts(new HashSet<>(products));
        
        return orderMapper.orderToOrderDTO(orderRepository.save(order));
    }
    
    @GetMapping("/by-product/{productId}")
    @Cacheable(value = "ordersByProduct", key = "#productId")
    public List<OrderDTO> getOrdersByProductId(@PathVariable Long productId) {
        List<Order> orders = orderRepository.findOrdersByProductId(productId);
        return orderMapper.ordersToOrderDTOs(orders);
    }
}
