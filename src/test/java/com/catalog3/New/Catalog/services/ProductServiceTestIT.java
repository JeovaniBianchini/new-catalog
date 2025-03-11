package com.catalog3.New.Catalog.services;

import com.catalog3.New.Catalog.dtos.ProductDto;
import com.catalog3.New.Catalog.repositories.ProductRepository;
import com.catalog3.New.Catalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProductServiceTestIT {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private long existId;
    private long nonExistId;
    private long totalCountProduct;

    @BeforeEach
    void setUp(){
        existId = 1L;
        nonExistId = 1000L;
        totalCountProduct = 25L;
    }

    @Test
    public void deleteShouldDeleteResourceWhenIdExist(){

        productService.delete(existId);

        Assertions.assertEquals(totalCountProduct - 1, productRepository.count());
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdNonExist(){

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(nonExistId);
        });
    }

    @Test
    public void findAllPagedShouldReturnPageWhenPage0Size10(){

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ProductDto> result = productService.findAllPaged(pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(10, result.getSize());
        Assertions.assertEquals(totalCountProduct, result.getTotalElements());
    }

    @Test
    public void findAllPagedShouldReturnEmptyWhenPageDoesNotExist(){

        PageRequest pageRequest = PageRequest.of(50, 10);
        Page<ProductDto> result = productService.findAllPaged(pageRequest);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void findAllPagedShouldReturnSortedPageWhenSortByName(){

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
        Page<ProductDto> result = productService.findAllPaged(pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
    }
}
