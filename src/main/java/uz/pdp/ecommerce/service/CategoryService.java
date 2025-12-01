package uz.pdp.ecommerce.service;

import org.springframework.transaction.annotation.Transactional;
import uz.pdp.ecommerce.entity.Category;
import uz.pdp.ecommerce.entity.Order;
import uz.pdp.ecommerce.entity.OrderItem;
import uz.pdp.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    public long count() {
        return categoryRepository.count();
    }
}



