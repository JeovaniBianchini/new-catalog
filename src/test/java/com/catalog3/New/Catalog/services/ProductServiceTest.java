package com.catalog3.New.Catalog.services;

import com.catalog3.New.Catalog.repositories.ProductRepository;
import com.catalog3.New.Catalog.services.exceptions.DatabaseException;
import com.catalog3.New.Catalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private long existId;
    private long nonExistId;
    private long dependentId;

    @BeforeEach
    void setUp(){
        existId = 1L;
        nonExistId = 1000L;
        dependentId = 2L;

        Mockito.when(productRepository.existsById(existId)).thenReturn(true);
        Mockito.when(productRepository.existsById(nonExistId)).thenReturn(false);
        Mockito.when(productRepository.existsById(dependentId)).thenReturn(true);

        Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists(){
        Assertions.assertDoesNotThrow(() -> {
            productService.delete(existId);
        });

    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists(){

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistId);
        });
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId(){

        Assertions.assertThrows(DatabaseException.class, () -> {
            productService.delete(dependentId);
        });
    }
}
