package com.catalog3.New.Catalog.services.exceptions;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String msg){
        super(msg);
    }
}
