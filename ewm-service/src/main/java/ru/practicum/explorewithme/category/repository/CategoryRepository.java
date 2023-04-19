package ru.practicum.explorewithme.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.category.model.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findCategoriesByIdIn(List<Long> ids);

    Long removeById(Long id);
}