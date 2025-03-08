package com.flab.ecommerce.domain.category.controller;

import com.flab.ecommerce.domain.category.dto.CategoryCreateRequestDTO;
import com.flab.ecommerce.domain.category.dto.CategoryResponseDTO;
import com.flab.ecommerce.domain.category.dto.CategoryUpdateRequestDTO;
import com.flab.ecommerce.domain.category.service.CategoryService;
import com.flab.ecommerce.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "카테고리", description = "카테고리 관리 기능을 제공합니다.")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "활성화된 카테고리 목록 조회", description = "사용자 및 관리자가 조회할 수 있는 활성화된 카테고리 목록을 조회합니다.")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getActiveCategories() {
        List<CategoryResponseDTO> categories = categoryService.getActiveCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @Operation(summary = "전체 카테고리 조회 (관리자 전용)", description = "관리자가 모든 카테고리 목록을 조회합니다.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getAllCategories() {
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @Operation(summary = "서브 카테고리 조회 (관리자 전용)", description = "관리자가 특정 카테고리의 하위 카테고리를 조회합니다.")
    @GetMapping("/{id}/sub-categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> getSubCategories(@PathVariable long id) {
        List<CategoryResponseDTO> subCategories = categoryService.getSubCategories(id);
        return ResponseEntity.ok(ApiResponse.success(subCategories));
    }


    @Operation(summary = "카테고리 생성 (관리자 전용)", description = "관리자가 새로운 카테고리를 등록합니다.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> createCategory(@RequestBody @Valid CategoryCreateRequestDTO requestDTO) {
        CategoryResponseDTO category = categoryService.createCategory(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(category));
    }

    @Operation(summary = "카테고리 수정 (관리자 전용)", description = "관리자가 특정 카테고리를 수정합니다.")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> updateCategory(@PathVariable long id, @RequestBody @Valid CategoryUpdateRequestDTO requestDTO) {
        CategoryResponseDTO category = categoryService.updateCategory(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(category));
    }

    @Operation(summary = "카테고리 활성화 (관리자 전용)", description = "관리자가 특정 카테고리를 활성화 처리합니다.")
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> activateCategory(@PathVariable long id) {
        categoryService.activateCategory(id);
        return ResponseEntity.ok(ApiResponse.success("카테고리 활성화가 완료되었습니다."));
    }

    @Operation(summary = "카테고리 비활성화 (관리자 전용)", description = "관리자가 특정 카테고리를 비활성화 처리합니다.")
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deactivateCategory(@PathVariable long id) {
        categoryService.deactivateCategory(id);
        return ResponseEntity.ok(ApiResponse.success("카테고리 비활성화가 완료되었습니다."));
    }




}
