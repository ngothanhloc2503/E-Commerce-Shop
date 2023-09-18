package com.shopping.admin.product;

import com.shopping.admin.product.repository.ProductRepository;
import com.shopping.common.entity.Brand;
import com.shopping.common.entity.Category;
import com.shopping.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void testCreateProduct() {
        Brand brand = testEntityManager.find(Brand.class, 37);
        Category category = testEntityManager.find(Category.class, 5);

        Product product = new Product();
        product.setName("Acer Aspire Desktop");
        product.setAlias("acer_aspire_desktop");
        product.setShortDescription("Short description for Acer Aspire Desktop");
        product.setFullDescription("Full description for Acer Aspire Desktop");

        product.setBrand(brand);
        product.setCategory(category);

        product.setPrice(678);
        product.setCost(600);
        product.setEnabled(true);
        product.setInStock(true);

        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllProduct() {
        Iterable<Product> iterable = productRepository.findAll();
        iterable.forEach(System.out::println);

        assertThat(iterable).isNotNull();
    }

    @Test
    public void testGetProductById() {
        Product product = productRepository.findById(1).get();
        System.out.println(product);

        assertThat(product).isNotNull();
    }

    @Test
    public void testUpdateProduct() {
        String newName = "Dell Inspiron 3001";
        Product product = productRepository.findById(2).get();
        product.setName(newName);

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo(newName);
        assertThat(savedProduct.getId()).isEqualTo(2);
    }

    @Test
    public void testDeleteProduct() {
        Integer id = 2;
        productRepository.deleteById(id);

        Optional<Product> result = productRepository.findById(id);

        assertThat(result.isEmpty());
    }

    @Test
    public void testFindByName() {
        Product product = productRepository.findByName("Samsung Galaxy A31");
        System.out.println(product);

        assertThat(product).isNotNull();
    }

    @Test
    public void testSaveProductWithImages() {
        Integer productId = 1;
        Product product = productRepository.findById(productId).get();

        product.setMainImage("main image.jpg");
        product.addExtraImage("extra image 1.jpg");
        product.addExtraImage("extra_image_2.jpg");
        product.addExtraImage("extra-image3.jpg");

        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct.getImages().size()).isEqualTo(3);
    }
}
