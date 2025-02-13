package com.catalog3.New.Catalog.services;

import com.catalog3.New.Catalog.dtos.CategoryDto;
import com.catalog3.New.Catalog.entities.Category;
import com.catalog3.New.Catalog.repositories.CategoryRepository;
import com.catalog3.New.Catalog.services.exceptions.DatabaseException;
import com.catalog3.New.Catalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDto> findAll(){
        List<Category> list = categoryRepository.findAll();
        return list.stream().map(x -> new CategoryDto(x)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDto findById(Long id) {
        Optional<Category> obj = categoryRepository.findById(id);
        Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new CategoryDto(entity);
    }

    @Transactional(readOnly = true)
    public CategoryDto insert(CategoryDto dto) {
        Category entity = new Category();
        entity.setName(dto.getName());
        entity = categoryRepository.save(entity);
        return new CategoryDto(entity);
    }

    @Transactional
    public CategoryDto update(Long id, CategoryDto dto) {
        try{
            Category entity = categoryRepository.getReferenceById(id);
            entity.setName(dto.getName());
            entity = categoryRepository.save(entity);
            return new CategoryDto(entity);
        }
        catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found");
        }
        try {
            categoryRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Fail integrity reference");
        }
    }

}
