package com.catalog3.New.Catalog.repositories;

import com.catalog3.New.Catalog.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
