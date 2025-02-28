package com.flab.ecommerce.domain.category.controller;

import com.flab.ecommerce.domain.category.dto.CategoryCreateRequestDTO;
import com.flab.ecommerce.domain.category.dto.CategoryResponseDTO;
import com.flab.ecommerce.domain.category.dto.CategoryUpdateRequestDTO;
import com.flab.ecommerce.domain.category.service.CategoryService;
import com.flab.ecommerce.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getActiveCategories() {
        List<CategoryResponseDTO> categories = categoryService.getActiveCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getAllCategories() {
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/{id}/sub-categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getSubCategories(@PathVariable long id) {
        List<CategoryResponseDTO> subCategories = categoryService.getSubCategories(id);
        return ResponseEntity.ok(ApiResponse.success(subCategories));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory(@Valid @RequestBody CategoryCreateRequestDTO requestDTO) {
        CategoryResponseDTO category = categoryService.createCategory(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(category));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> updateCategory(@PathVariable long id, @RequestBody CategoryUpdateRequestDTO requestDTO) {
        CategoryResponseDTO category = categoryService.updateCategory(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(category));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> activateCategory(@PathVariable long id) {
        categoryService.activateCategory(id);
        return ResponseEntity.ok(ApiResponse.success("카테고리 활성화가 완료되었습니다."));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deactivateCategory(@PathVariable long id) {
        categoryService.deactivateCategory(id);
        return ResponseEntity.ok(ApiResponse.success("카테고리 비활성화가 완료되었습니다."));
    }




}
