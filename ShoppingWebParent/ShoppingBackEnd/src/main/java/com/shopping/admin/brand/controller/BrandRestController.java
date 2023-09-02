package com.shopping.admin.brand.controller;

import com.shopping.common.exception.BrandNotFoundException;
import com.shopping.admin.brand.BrandNotFoundRestException;
import com.shopping.admin.brand.DTO.CategoryDTO;
import com.shopping.admin.brand.service.BrandService;
import com.shopping.common.entity.Brand;
import com.shopping.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class BrandRestController {

    @Autowired
    private BrandService brandService;

    @PostMapping("/brands/check_unique")
    public String checkUnique(@Param("id") Integer id, @Param("name") String name) {
        return brandService.checkUnique(id, name);
    }

    @GetMapping("/brands/{id}/categories")
    public List<CategoryDTO> listCategoriesByBrand(@PathVariable("id") Integer id) throws BrandNotFoundRestException {
        List<CategoryDTO> listCategories = new ArrayList<>();

        try {
            Brand brand = brandService.getBrandById(id);
            Set<Category> categories = brand.getCategories();
            for (Category category : categories) {
                CategoryDTO categoryDTO = new CategoryDTO(category.getId(), category.getName());
                listCategories.add(categoryDTO);
            }

            return listCategories;
        } catch (BrandNotFoundException e) {
            throw new BrandNotFoundRestException();
        }
    }
}
