package com.example.FakeCommerce.schema;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "orders")
public class Order extends BaseEntity {

    private OrderStatus status;

    @ManyToMany
    @JoinTable(name = "order_products", joinColumns = @JoinColumn(name = "order_id"), // the FK belonging to the same  schema -- Order
            inverseJoinColumns = @JoinColumn(name = "product_id") // the FK belonging to the other schema -- Product
    )
    private List<Product> products;

}
