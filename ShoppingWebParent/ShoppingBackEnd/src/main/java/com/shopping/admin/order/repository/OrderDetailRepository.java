package com.shopping.admin.order.repository;

import com.shopping.common.entity.order.OrderDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface OrderDetailRepository extends CrudRepository<OrderDetail, Integer> {

    @Query("SELECT NEW com.shopping.common.entity.order.OrderDetail(o.product.category.name, o.quantity,"
            + " o.productCost, o.shippingCost, o.subtotal) FROM OrderDetail o"
            + " WHERE o.order.orderTime BETWEEN ?1 AND ?2")
    public List<OrderDetail> findWithCategoryAndTimeBetween(Date startTime, Date endTime);

    @Query("SELECT NEW com.shopping.common.entity.order.OrderDetail(o.quantity, o.product.name,"
            + " o.productCost, o.shippingCost, o.subtotal) FROM OrderDetail o"
            + " WHERE o.order.orderTime BETWEEN ?1 AND ?2")
    public List<OrderDetail> findWithProductAndTimeBetween(Date startTime, Date endTime);
}
