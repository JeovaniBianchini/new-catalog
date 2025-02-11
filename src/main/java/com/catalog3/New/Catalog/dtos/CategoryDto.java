package com.catalog3.New.Catalog.dtos;

import com.catalog3.New.Catalog.entities.Category;

public class CategoryDto  {

    private Long id;
    private String name;

    public CategoryDto(){
    }

    public CategoryDto(Category entity){
        id = entity.getId();
        name = entity.getName();
    }

    public CategoryDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
