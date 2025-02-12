package com.catalog3.New.Catalog.controllers;

import com.catalog3.New.Catalog.dtos.CategoryDto;
import com.catalog3.New.Catalog.entities.Category;
import com.catalog3.New.Catalog.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> findAll(){
        List<CategoryDto> list = categoryService.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoryDto> findById(@PathVariable Long id){
        CategoryDto dto = categoryService.findById(id);
        return ResponseEntity.ok().body(dto);
    }

}
