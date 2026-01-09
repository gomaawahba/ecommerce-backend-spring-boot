package com.gomaa.service;

import com.gomaa.dto.CategoryDTO;
import com.gomaa.model.Category;
import com.gomaa.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // تحويل DTO → Entity يدويًا
    private Category toEntity(CategoryDTO dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    // تحويل Entity → DTO يدويًا
    private CategoryDTO toDto(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public CategoryDTO create(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new RuntimeException("Category already exists");
        }

        Category category = toEntity(categoryDTO);
        Category saved = categoryRepository.save(category);

        return toDto(saved);
    }

    public List<CategoryDTO> allCategory() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return toDto(category);
    }
}
