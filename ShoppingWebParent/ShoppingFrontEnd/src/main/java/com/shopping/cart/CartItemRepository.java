package com.shopping.cart;

import com.shopping.common.entity.CartItem;
import com.shopping.common.entity.Customer;
import com.shopping.common.entity.product.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CartItemRepository extends CrudRepository<CartItem, Integer> {

    public List<CartItem> findByCustomer(Customer customer);

    public CartItem findByCustomerAndProduct(Customer customer, Product product);

    @Query("UPDATE CartItem c SET c.quantity = ?1 WHERE c.customer.id = ?2 AND c.product.id = ?3")
    @Modifying
    public void updateQuantity(int quantity, Integer customerId, Integer productId);

    @Query("DELETE FROM CartItem c WHERE c.customer.id = ?1 AND c.product.id = ?2")
    @Modifying
    public void deleteByCustomerAndProduct(Integer customerId, Integer productId);

    @Query("DELETE FROM CartItem c WHERE c.customer.id = ?1")
    @Modifying
    public void deleteByCustomer(Integer customerId);
}
