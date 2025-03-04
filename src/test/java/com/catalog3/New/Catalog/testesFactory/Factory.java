package com.catalog3.New.Catalog.testesFactory;

import com.catalog3.New.Catalog.dtos.ProductDto;
import com.catalog3.New.Catalog.entities.Category;
import com.catalog3.New.Catalog.entities.Product;

import java.time.Instant;

public class Factory {


    public static Product createProduct(){
        Product product = new Product(1L,"PC Gamer Master X", "Lorem......", 5000.0,"https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/25-big.jpg", Instant.parse("2020-07-14T10:00:00Z"));
        product.getCategories().add(new Category(2L, "Eletronics"));
        return product;
    }

    public static ProductDto creteProductDto(){
        Product product = createProduct();
        ProductDto dto = new ProductDto(product, product.getCategories());
        return dto;
    }

}
