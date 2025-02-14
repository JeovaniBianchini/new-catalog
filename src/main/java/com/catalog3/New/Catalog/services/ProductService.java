package com.catalog3.New.Catalog.services;

import com.catalog3.New.Catalog.dtos.CategoryDto;
import com.catalog3.New.Catalog.dtos.ProductDto;
import com.catalog3.New.Catalog.entities.Category;
import com.catalog3.New.Catalog.entities.Product;
import com.catalog3.New.Catalog.repositories.CategoryRepository;
import com.catalog3.New.Catalog.repositories.ProductRepository;
import com.catalog3.New.Catalog.services.exceptions.DatabaseException;
import com.catalog3.New.Catalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @Transactional(readOnly = true)
    public Page<ProductDto> findAllPaged(Pageable pageable){
        Page<Product> list = productRepository.findAll(pageable);
        return list.map(x -> new ProductDto(x));
    }

    @Transactional(readOnly = true)
    public ProductDto findById(Long id){
        Optional<Product> productOptional = productRepository.findById(id);
        Product product = productOptional.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new ProductDto(product, product.getCategories());
    }

    @Transactional
    public ProductDto saveProduct(ProductDto productDto) {
        Product product = new Product();
        copyDtoToEntity(productDto, product);
        product = productRepository.save(product);
        return new ProductDto(product);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        try {
            Product product = productRepository.getReferenceById(id);
            copyDtoToEntity(productDto, product);
            product = productRepository.save(product);
            return new ProductDto(product);
        } catch (EntityNotFoundException e){
            throw  new ResourceNotFoundException("Id " + id + " not found ");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found");
        }
        try {
            productRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Fail integrity reference");
        }
    }

    private void copyDtoToEntity(ProductDto productDto, Product product) {

        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setDate(productDto.getDate());
        product.setPrice(productDto.getPrice());
        product.setImageUrl(productDto.getImageUrl());

        product.getCategories().clear();
        for (CategoryDto catDto: productDto.getCategories()){
            Category category = categoryRepository.getReferenceById(catDto.getId());
            product.getCategories().add(category);
        }
    }

}
