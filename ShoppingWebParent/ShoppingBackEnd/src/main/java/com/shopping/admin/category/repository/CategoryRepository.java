package com.shopping.admin.category.repository;

import com.shopping.common.entity.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Integer>,
                                            PagingAndSortingRepository<Category, Integer> {

    @Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
    public List<Category> findRootCategories();
}
