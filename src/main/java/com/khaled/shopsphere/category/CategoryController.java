package com.khaled.shopsphere.category;

import com.khaled.shopsphere.category.request.CreateCategoryRequest;
import com.khaled.shopsphere.category.request.UpdateCategoryRequest;
import com.khaled.shopsphere.category.response.CategoryResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "categories API")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('admin:access')")
    public ResponseEntity<CategoryResponse> create(
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.create(request));
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasAuthority('admin:access')")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable UUID categoryId,
            @Valid @RequestBody UpdateCategoryRequest request
    ) {
        return ResponseEntity.ok(
                categoryService.update(categoryId, request)
        );
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasAuthority('admin:access')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID categoryId
    ) {
        categoryService.delete(categoryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getById(
            @PathVariable UUID categoryId
    ) {
        return ResponseEntity.ok(categoryService.getById(categoryId));
    }
}
