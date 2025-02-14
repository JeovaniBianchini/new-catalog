package com.catalog3.New.Catalog.repositories;

import com.catalog3.New.Catalog.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
