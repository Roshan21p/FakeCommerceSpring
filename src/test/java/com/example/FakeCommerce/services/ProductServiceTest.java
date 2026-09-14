package com.example.FakeCommerce.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.FakeCommerce.dtos.CreateProductRequestDto;
import com.example.FakeCommerce.dtos.GetProductResponseDto;
import com.example.FakeCommerce.dtos.GetProductWithDetailsResponseDto;
import com.example.FakeCommerce.exceptions.ResourceNotFoundException;
import com.example.FakeCommerce.repositories.CategoryRepository;
import com.example.FakeCommerce.repositories.ProductRepository;
import com.example.FakeCommerce.schema.Category;
import com.example.FakeCommerce.schema.Product;
import com.example.FakeCommerce.services.cache.ProductRedisCache;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductRedisCache productRedisCache;

    @InjectMocks
    private ProductService productService;

    @Test
    void getAllProducts_whenCached_returnsCachedProductsWithoutRepositoryCall() {
        // Arrange
        List<GetProductResponseDto> cachedProducts = List.of(
                GetProductResponseDto.builder().id(1L).title("Cached product").build());
        when(productRedisCache.getAllProducts()).thenReturn(Optional.of(cachedProducts));

        // Act
        List<GetProductResponseDto> result = productService.getAllProducts();

        // Assert
        assertEquals(cachedProducts, result);
        verify(productRepository, never()).findAll();
    }

    @Test
    void getAllProducts_whenCacheMiss_mapsProductsAndCachesResult() {
        // Arrange
        Product product = product(1L, "Laptop");
        when(productRedisCache.getAllProducts()).thenReturn(Optional.empty());
        when(productRepository.findAll()).thenReturn(List.of(product));

        // Act
        List<GetProductResponseDto> result = productService.getAllProducts();

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Laptop", result.get(0).getTitle());
        verify(productRedisCache).putAllProducts(result);
    }

    @Test
    void getAllProducts_whenCacheMissAndDatabaseEmpty_returnsEmptyListAndCachesIt() {
        // Arrange
        when(productRedisCache.getAllProducts()).thenReturn(Optional.empty());
        when(productRepository.findAll()).thenReturn(List.of());

        // Act
        List<GetProductResponseDto> result = productService.getAllProducts();

        // Assert
        assertEquals(List.of(), result);
        verify(productRedisCache).putAllProducts(result);
    }

    @Test
    void getProductById_whenCached_returnsCachedProductWithoutRepositoryCall() {
        // Arrange
        GetProductResponseDto cachedProduct = GetProductResponseDto.builder()
                .id(1L)
                .title("Cached product")
                .build();
        when(productRedisCache.getSummary(1L)).thenReturn(Optional.of(cachedProduct));

        // Act
        GetProductResponseDto result = productService.getProductById(1L);

        // Assert
        assertEquals(cachedProduct, result);
        verify(productRepository, never()).findById(1L);
    }

    @Test
    void getProductById_whenMissing_throwsException() {
        // Arrange
        when(productRedisCache.getSummary(1L)).thenReturn(Optional.empty());
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(1L));

        assertEquals("Product not found with id: 1", exception.getMessage());
    }

    @Test
    void getProductById_whenCacheMissAndProductExists_returnsMappedProductAndCachesIt() {
        // Arrange
        Product product = product(1L, "Laptop");
        when(productRedisCache.getSummary(1L)).thenReturn(Optional.empty());
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        GetProductResponseDto result = productService.getProductById(1L);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getTitle());
        verify(productRedisCache).putSummary(1L, result);
    }

    @Test
    void getProductWithDetails_whenCacheMiss_mapsAndCachesDetails() {
        // Arrange
        Category category = Category.builder().name("Electronics").build();
        Product product = product(1L, "Laptop");
        product.setCategory(category);
        when(productRedisCache.getProductWithDetails(1L)).thenReturn(Optional.empty());
        when(productRepository.findProductWithDetailsById(1L)).thenReturn(List.of(product));

        // Act
        GetProductWithDetailsResponseDto result = productService.getProductWithDetails(1L);

        // Assert
        assertEquals("Laptop", result.getTitle());
        assertEquals("Electronics", result.getCategory());
        verify(productRedisCache).putProductWithDetails(1L, result);
    }

    @Test
    void getProductWithDetails_whenMissing_throwsException() {
        // Arrange
        when(productRedisCache.getProductWithDetails(1L)).thenReturn(Optional.empty());
        when(productRepository.findProductWithDetailsById(1L)).thenReturn(List.of());

        // Act and Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductWithDetails(1L));

        assertEquals("Product not found with id: 1", exception.getMessage());
    }

    @Test
    void getProductWithDetails_whenCached_returnsCachedDetailsWithoutRepositoryCall() {
        // Arrange
        GetProductWithDetailsResponseDto cachedDetails = GetProductWithDetailsResponseDto.builder()
                .id(1L).title("Cached laptop").build();
        when(productRedisCache.getProductWithDetails(1L)).thenReturn(Optional.of(cachedDetails));

        // Act
        GetProductWithDetailsResponseDto result = productService.getProductWithDetails(1L);

        // Assert
        assertEquals(cachedDetails, result);
        verify(productRepository, never()).findProductWithDetailsById(1L);
    }

    @Test
    void createProduct_fetchesCategoryAndSavesProduct() {
        // Arrange
        Category category = Category.builder().name("Electronics").build();
        CreateProductRequestDto request = new CreateProductRequestDto(
                "Laptop", "Work laptop", new BigDecimal("999.99"), "laptop.png", 3L,
                new BigDecimal("4.5"));
        when(categoryService.getCategoryById(3L)).thenReturn(category);
        when(productRepository.save(org.mockito.ArgumentMatchers.any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Product result = productService.createProduct(request);

        // Assert
        assertEquals("Laptop", result.getTitle());
        assertEquals(new BigDecimal("999.99"), result.getPrice());
        assertEquals(category, result.getCategory());
        verify(categoryService).getCategoryById(3L);
    }

    @Test
    void createProduct_whenCategoryMissing_propagatesNotFoundException() {
        // Arrange
        ResourceNotFoundException expected = new ResourceNotFoundException("Category not found with id: 3");
        CreateProductRequestDto request = new CreateProductRequestDto(
                "Laptop", "Work laptop", new BigDecimal("999.99"), "laptop.png", 3L,
                new BigDecimal("4.5"));
        when(categoryService.getCategoryById(3L)).thenThrow(expected);

        // Act and Assert
        assertEquals(expected, assertThrows(ResourceNotFoundException.class,
                () -> productService.createProduct(request)));
        verify(productRepository, never()).save(org.mockito.ArgumentMatchers.any(Product.class));
    }

    @Test
    void deleteProduct_whenProductExists_deletesProduct() {
        // Arrange
        Product product = product(1L, "Laptop");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_whenProductMissing_throwsException() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act and Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.deleteProduct(1L));

        assertEquals("Product not found with id: 1", exception.getMessage());
    }

    @Test
    void getProductsByCategory_whenCategoryExists_returnsProducts() {
        // Arrange
        Category category = Category.builder().name("Electronics").build();
        List<Product> products = List.of(product(1L, "Laptop"));
        when(categoryRepository.findByName("Electronics")).thenReturn(category);
        when(productRepository.findByCategory(category)).thenReturn(products);

        // Act and Assert
        assertEquals(products, productService.getProductsByCategory("Electronics"));
    }

    @Test
    void getProductsByCategory_whenCategoryHasNoProducts_returnsEmptyList() {
        // Arrange
        Category category = Category.builder().name("Books").build();
        when(categoryRepository.findByName("Books")).thenReturn(category);
        when(productRepository.findByCategory(category)).thenReturn(List.of());

        // Act
        List<Product> result = productService.getProductsByCategory("Books");

        // Assert
        assertEquals(List.of(), result);
    }

    @Test
    void getProductsByCategory_whenCategoryMissing_throwsException() {
        // Arrange
        when(categoryRepository.findByName("Unknown")).thenReturn(null);

        // Act and Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductsByCategory("Unknown"));

        assertEquals("Category not found with name: Unknown", exception.getMessage());
        verify(productRepository, never()).findByCategory(org.mockito.ArgumentMatchers.any(Category.class));
    }

    @Test
    void getAllCategories_delegatesToRepository() {
        // Arrange
        when(productRepository.findAllCategories()).thenReturn(List.of("Electronics", "Books"));

        // Act
        List<String> result = productService.getAllCategories();

        // Assert
        assertEquals(List.of("Electronics", "Books"), result);
    }

    @Test
    void getAllCategories_whenRepositoryIsEmpty_returnsEmptyList() {
        // Arrange
        when(productRepository.findAllCategories()).thenReturn(List.of());

        // Act
        List<String> result = productService.getAllCategories();

        // Assert
        assertEquals(List.of(), result);
    }

    private Product product(Long id, String title) {
        Product product = Product.builder()
                .title(title)
                .description("Description")
                .price(new BigDecimal("10.00"))
                .image("image.png")
                .rating(new BigDecimal("4.0"))
                .build();
        product.setId(id);
        return product;
    }
}