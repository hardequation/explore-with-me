package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dal.CategoryRepository;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.dto.category.UpdateCategoryDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;

import java.util.List;
import java.util.Optional;

import static ru.practicum.ewm.exception.ErrorMessages.CATEGORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    public List<CategoryDto> findAll() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::map)
                .toList();
    }

    public List<CategoryDto> getCategories(int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        return categoryMapper.map(categoryRepository.findAll(page).toList());
    }

    public CategoryDto findById(int id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND + id));
        return categoryMapper.map(category);
    }

    public CategoryDto add(NewCategoryDto dto) {
        checkUniqueness(dto.getName());
        Category toCreate = categoryMapper.map(dto);
        Category created = categoryRepository.save(toCreate);
        return categoryMapper.map(created);
    }

    public CategoryDto update(int catId, UpdateCategoryDto dto) {
        Category toUpdate = categoryMapper.map(catId, dto);
        Category updated = categoryRepository.save(toUpdate);
        return categoryMapper.map(updated);
    }

    public void delete(int id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isEmpty()) {
            return;
        }

        Category category = categoryOptional.get();
        if (!category.getEvents().isEmpty()) {
            throw new ValidationException(
                    String.format("It's impossible to remove category %s, which is used in some event", category.getName()));
        }
        categoryRepository.deleteById(id);
    }

    private void checkUniqueness(String categoryName) {
        if (categoryRepository.findAll().stream()
                .anyMatch(cat -> cat.getName().equals(categoryName))) {
            throw new ValidationException(
                    String.format("Category with name %s already exists", categoryName));
        }
    }
}
