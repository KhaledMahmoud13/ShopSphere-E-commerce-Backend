package com.khaled.shopsphere.category.impl;

import com.khaled.shopsphere.category.Category;
import com.khaled.shopsphere.category.CategoryMapper;
import com.khaled.shopsphere.category.CategoryRepository;
import com.khaled.shopsphere.category.CategoryService;
import com.khaled.shopsphere.category.request.CreateCategoryRequest;
import com.khaled.shopsphere.category.request.UpdateCategoryRequest;
import com.khaled.shopsphere.category.response.CategoryResponse;
import com.khaled.shopsphere.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.khaled.shopsphere.exception.ErrorCode.CATEGORY_ALREADY_EXISTS;
import static com.khaled.shopsphere.exception.ErrorCode.CATEGORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponse create(CreateCategoryRequest request) {

        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BusinessException(CATEGORY_ALREADY_EXISTS);
        }

        Category category = Category.builder()
                .name(request.getName())
                .build();

        return categoryMapper.toResponse(
                categoryRepository.save(category)
        );
    }

    @Override
    @Transactional
    public CategoryResponse update(UUID categoryId, UpdateCategoryRequest request) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(CATEGORY_NOT_FOUND));

        if (!category.getName().equalsIgnoreCase(request.getName())
                && categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BusinessException(CATEGORY_ALREADY_EXISTS);
        }

        category.setName(request.getName());

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void delete(UUID categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(CATEGORY_NOT_FOUND));

        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(UUID categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(CATEGORY_NOT_FOUND));

        return categoryMapper.toResponse(category);
    }
}
