package com.shopping.address;

import com.shopping.common.entity.Address;
import com.shopping.common.entity.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AddressRepository extends CrudRepository<Address, Integer> {

    public List<Address> findByCustomer(Customer customer);

    @Query("SELECT a FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
    public Address findByIdAndCustomer(Integer id, Integer customerId);

    @Query("DELETE FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
    @Modifying
    public void deleteByIdAndCustomer(Integer id, Integer customerId);

    @Query("UPDATE Address a SET a.defaultForShipping = true WHERE a.id = ?1")
    @Modifying
    public void setDefaultAddress(Integer id);

    @Query("UPDATE Address a SET a.defaultForShipping = false WHERE a.id != ?1 AND a.customer.id = ?2")
    @Modifying
    public void setNonDefaultForOthers(Integer defaultAddressId, Integer customerId);
}
