package com.flab.ecommerce.domain.category.service;

import com.flab.ecommerce.domain.category.dto.CategoryCreateRequestDTO;
import com.flab.ecommerce.domain.category.dto.CategoryResponseDTO;
import com.flab.ecommerce.domain.category.dto.CategoryUpdateRequestDTO;
import com.flab.ecommerce.domain.category.entity.Category;
import com.flab.ecommerce.domain.category.exception.CategoryAlreadyExistsException;
import com.flab.ecommerce.domain.category.exception.CategoryNotFoundException;
import com.flab.ecommerce.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponseDTO> getActiveCategories() {
        return categoryRepository.findByIsActiveTrue().stream()
                .map(CategoryResponseDTO::new)
                .toList();
    }

    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(CategoryResponseDTO::new)
                .toList();
    }

    public List<CategoryResponseDTO> getSubCategories(long id) {
        Category parent = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
        return categoryRepository.findByParentCategory(parent).stream()
                .map(CategoryResponseDTO::new)
                .toList();
    }

    @Transactional
    public CategoryResponseDTO createCategory(CategoryCreateRequestDTO requestDTO) {
        if(categoryRepository.existsByName(requestDTO.getName())) {
            throw new CategoryAlreadyExistsException();
        }
        Category parent = null;
        if (requestDTO.getParentCategoryId() != null) {
            parent = categoryRepository.findById(requestDTO.getParentCategoryId())
                    .orElseThrow(CategoryNotFoundException::new);
        }

        Category category = Category.builder()
                .name(requestDTO.getName())
                .parentCategory(parent)
                .isActive(true)
                .build();

        Category saved = categoryRepository.save(category);
        return new CategoryResponseDTO(saved);
    }

    @Transactional
    public CategoryResponseDTO updateCategory(long id, CategoryUpdateRequestDTO requestDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);

        if (requestDTO.getName() != null && !requestDTO.getName().equals(category.getName())) {
            if(categoryRepository.existsByNameAndIdNot(requestDTO.getName(), id)) {
                throw new CategoryAlreadyExistsException();
            }
            category.updateName(requestDTO.getName());
        }

        if (requestDTO.getParentCategoryId() != null) {
            Long currentParentId = category.getParentCategory() != null ? category.getParentCategory().getId() : null;
            if (!requestDTO.getParentCategoryId().equals(currentParentId)) {
                Category parent = categoryRepository.findById(requestDTO.getParentCategoryId())
                        .orElseThrow(CategoryNotFoundException::new);
                category.updateParentCategory(parent);
            }
        }

        return new CategoryResponseDTO(category);
    }

    @Transactional
    public void activateCategory(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
        category.activate();
    }

    @Transactional
    public void deactivateCategory(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
        category.deactivate();
    }

}
