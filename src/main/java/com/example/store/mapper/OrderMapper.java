package com.example.store.mapper;

import com.example.store.dto.OrderCustomerDTO;
import com.example.store.dto.OrderDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.entity.Product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    // Entity to DTO (for responses)
    @Mapping(target = "productIds", source = "products", qualifiedByName = "productsToProductIds")
    OrderDTO orderToOrderDTO(Order order);
    
    List<OrderDTO> ordersToOrderDTOs(List<Order> orders);
    
    // DTO to Entity (for requests)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", source = "productIds", qualifiedByName = "productIdsToProducts")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "orderCustomerDTOToCustomer")
    Order orderDTOToOrder(OrderDTO orderDTO);
    
    // Helper mappings
    OrderCustomerDTO customerToOrderCustomerDTO(Customer customer);
    
    @Named("orderCustomerDTOToCustomer")
    default Customer orderCustomerDTOToCustomer(OrderCustomerDTO customerDTO) {
        if (customerDTO == null) return null;
        Customer customer = new Customer();
        customer.setId(customerDTO.getId());
        return customer;
    }
    
    @Named("productsToProductIds")
    default List<Long> productsToProductIds(Set<Product> products) {
        if (products == null) return null;
        return products.stream()
            .map(Product::getId)
            .sorted()
            .collect(Collectors.toList());
    }
    
    @Named("productIdsToProducts")
    default Set<Product> productIdsToProducts(List<Long> productIds) {
        if (productIds == null) return new HashSet<>();
        return productIds.stream()
            .map(id -> {
                Product product = new Product();
                product.setId(id);
                return product;
            })
            .collect(Collectors.toSet());
    }
}
