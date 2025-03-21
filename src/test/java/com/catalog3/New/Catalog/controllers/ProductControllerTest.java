package com.catalog3.New.Catalog.controllers;

import com.catalog3.New.Catalog.dtos.ProductDto;
import com.catalog3.New.Catalog.services.ProductService;
import com.catalog3.New.Catalog.services.exceptions.DatabaseException;
import com.catalog3.New.Catalog.services.exceptions.ResourceNotFoundException;
import com.catalog3.New.Catalog.testesFactory.Factory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import javax.print.attribute.standard.Media;
import javax.xml.transform.Result;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private long existId;
    private long nonExistId;
    private long dependentId;
    private ProductDto productDto;
    private PageImpl<ProductDto> page;

    @BeforeEach
    void setUp() throws Exception{
        existId = 1L;
        nonExistId = 2L;
        dependentId = 3L;
        productDto = Factory.creteProductDto();
        page = new PageImpl<>(List.of(productDto));

        when(productService.findAllPaged(ArgumentMatchers.any())).thenReturn(page);

        when(productService.findById(existId)).thenReturn(productDto);
        when(productService.findById(nonExistId)).thenThrow(ResourceNotFoundException.class);

        when(productService.saveProduct(any())).thenReturn(productDto);

        when(productService.updateProduct(eq(existId), any())).thenReturn(productDto);
        when(productService.updateProduct(eq(nonExistId), any())).thenThrow(ResourceNotFoundException.class);

        doNothing().when(productService).delete(existId);
        doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistId);
        doThrow(DatabaseException.class).when(productService).delete(dependentId);
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

    @Test
    public void updateShouldReturnObjectWhenIdExist() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDto);

        ResultActions result = mockMvc.perform(put("/products/{1}", existId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void updateShouldThrowNotFoundWhenIdDoesNotExist() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDto);

        ResultActions result = mockMvc.perform(put("/products/{2}", nonExistId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void saveShouldReturnCreatedObject() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDto);

        ResultActions result = mockMvc.perform(post("/products")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void deleteDoNothingWhenIdExist() throws Exception {

        ResultActions result = mockMvc.perform(delete("/products/{1}", existId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    public void deleteThrowResourceNotFoundExceptionWhenIdDoesNotExist() throws Exception {

        ResultActions result = mockMvc.perform(delete("/products/{2}", nonExistId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }
}
