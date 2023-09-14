package com.shopping.cart;

import com.shopping.common.entity.CartItem;
import com.shopping.common.entity.Customer;
import com.shopping.common.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CartItemRepositoryTests {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void testCreateCartItem() {
        Customer customer = testEntityManager.find(Customer.class, 1);
        Product product = testEntityManager.find(Product.class, 1);

        CartItem cartItem = new CartItem(customer, product, 3);
        CartItem saved = cartItemRepository.save(cartItem);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindByCustomer() {
        Customer customer = new Customer(1);
        List<CartItem> byCustomer = cartItemRepository.findByCustomer(customer);
        byCustomer.forEach(System.out::println);

        assertThat(byCustomer).size().isGreaterThan(0);
    }

    @Test
    public void testFindByCustomerAndProduct() {
        Customer customer = new Customer(1);
        Product product = new Product(1);

        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);
        System.out.println(cartItem);

        assertThat(cartItem).isNotNull();
        assertThat(cartItem.getId()).isGreaterThan(0);
    }

    @Test
    public void testUpdateQuantity() {
        Integer customerId = 1;
        Integer productId = 1;
        Customer customer = new Customer(customerId);
        Product product = new Product(productId);
        int quantity = 2;

        cartItemRepository.updateQuantity(quantity, customerId, productId);

        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);

        assertThat(cartItem.getQuantity()).isEqualTo(quantity);
    }
}
