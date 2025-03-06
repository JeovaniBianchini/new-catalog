package com.catalog3.New.Catalog.controllers;

import com.catalog3.New.Catalog.dtos.ProductDto;
import com.catalog3.New.Catalog.services.ProductService;
import com.catalog3.New.Catalog.services.exceptions.ResourceNotFoundException;
import com.catalog3.New.Catalog.testesFactory.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private long existId;
    private long nonExistId;
    private ProductDto productDto;
    private PageImpl<ProductDto> page;

    @BeforeEach
    void setUp() throws Exception{
        existId = 1L;
        nonExistId = 2L;
        productDto = Factory.creteProductDto();
        page = new PageImpl<>(List.of(productDto));

        when(productService.findAllPaged(ArgumentMatchers.any())).thenReturn(page);

        when(productService.findById(existId)).thenReturn(productDto);
        when(productService.findById(nonExistId)).thenThrow(ResourceNotFoundException.class);
    }

    @Test
    public void findAllShouldReturnPage() throws Exception{
        mockMvc.perform(get("/products")).andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldReturnObjectWhenIdExist() throws Exception {
        ResultActions result = mockMvc.perform(get("/products/{1}", existId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions result = mockMvc.perform(get("/products/{2}", nonExistId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }
}
