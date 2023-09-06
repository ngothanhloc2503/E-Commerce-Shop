package com.shopping.admin.setting.repository;

import com.shopping.common.entity.Country;
import com.shopping.common.entity.State;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StateRepository extends CrudRepository<State, Integer> {

    public List<State> findByCountryOrderByNameAsc(Country country);
}
