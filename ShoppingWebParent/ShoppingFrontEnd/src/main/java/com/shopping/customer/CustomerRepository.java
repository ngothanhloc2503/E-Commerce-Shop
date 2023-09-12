package com.shopping.customer;

import com.shopping.common.entity.AuthenticationType;
import com.shopping.common.entity.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {

    public Customer findByEmail(String email);

    public Customer findByVerificationCode(String code);

    @Query("UPDATE Customer c SET c.enabled = true, c.verificationCode = null WHERE c.id = ?1")
    @Modifying
    public void enable(Integer id);

    @Query("UPDATE Customer c SET c.authenticationType = ?2 WHERE c.id = ?1")
    @Modifying
    public void updateAuthenticationType(Integer id, AuthenticationType authenticationType);

    public Customer findByResetPasswordToken(String token);
}
