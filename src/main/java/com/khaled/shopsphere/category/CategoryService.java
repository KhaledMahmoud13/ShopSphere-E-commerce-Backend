package com.khaled.shopsphere.category;

import com.khaled.shopsphere.category.request.CreateCategoryRequest;
import com.khaled.shopsphere.category.request.UpdateCategoryRequest;
import com.khaled.shopsphere.category.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryResponse create(CreateCategoryRequest request);

    CategoryResponse update(UUID categoryId, UpdateCategoryRequest request);

    void delete(UUID categoryId);

    List<CategoryResponse> getAll();

    CategoryResponse getById(UUID categoryId);
}
