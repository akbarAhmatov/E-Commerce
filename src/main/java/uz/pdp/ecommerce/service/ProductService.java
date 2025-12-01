package uz.pdp.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.pdp.ecommerce.entity.Product;
import uz.pdp.ecommerce.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public List<Product> findAll() {
        List<Product> products = productRepository.findAll();
        products.forEach(this::enrichProduct);
        return products;
    }

    public List<Product> findActiveProducts() {
        List<Product> products = productRepository.findByIsActiveTrue();
        products.forEach(this::enrichProduct);
        return products;
    }

    public Optional<Product> findById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        product.ifPresent(this::enrichProduct);
        return product;
    }

    public List<Product> findByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        products.forEach(this::enrichProduct);
        return products;
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    public long count() {
        return productRepository.count();
    }

    private void enrichProduct(Product product) {
        if (product.getCategoryId() != null) {
            categoryService.findById(product.getCategoryId())
                .ifPresent(category -> product.setCategoryName(category.getName()));
        }
    }
}
