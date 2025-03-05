package com.catalog3.New.Catalog.services;

import com.catalog3.New.Catalog.dtos.ProductDto;
import com.catalog3.New.Catalog.entities.Category;
import com.catalog3.New.Catalog.entities.Product;
import com.catalog3.New.Catalog.repositories.CategoryRepository;
import com.catalog3.New.Catalog.repositories.ProductRepository;
import com.catalog3.New.Catalog.services.exceptions.DatabaseException;
import com.catalog3.New.Catalog.services.exceptions.ResourceNotFoundException;
import com.catalog3.New.Catalog.testesFactory.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existId;
    private long nonExistId;
    private long dependentId;
    private Product product;
    private PageImpl<Product> page;
    private Category category;
    private ProductDto productDto;


    @BeforeEach
    void setUp(){
        existId = 1L;
        nonExistId = 1000L;
        dependentId = 2L;
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));
        category = Factory.createCategory();
        productDto = Factory.creteProductDto();

        Mockito.when(productRepository.existsById(existId)).thenReturn(true);
        Mockito.when(productRepository.existsById(nonExistId)).thenReturn(false);
        Mockito.when(productRepository.existsById(dependentId)).thenReturn(true);

        Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);

        Mockito.when(productRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(productRepository.getReferenceById(existId)).thenReturn(product);
        Mockito.when(productRepository.getReferenceById(nonExistId)).thenThrow(EntityNotFoundException.class);
        Mockito.when(categoryRepository.getReferenceById(existId)).thenReturn(category);
        Mockito.when(categoryRepository.getReferenceById(nonExistId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(productRepository.findById(existId)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findById(nonExistId)).thenReturn(Optional.empty());
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

    @Test
    public void findAllPagedShouldPage(){

        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductDto> result = productService.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(productRepository).findAll(pageable);
    }

    @Test
    public void findByIdShouldReturnObjectWhenIdExist(){

        ProductDto dto = productService.findById(existId);

        Assertions.assertNotNull(dto);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists(){

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(nonExistId);
        });
    }

    @Test
    public void updateShouldReturnObjectDtoWhenExistId(){

        ProductDto result = productService.updateProduct(existId, productDto);

        Assertions.assertNotNull(result);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists(){

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProduct(nonExistId, productDto);
        });
    }
}
