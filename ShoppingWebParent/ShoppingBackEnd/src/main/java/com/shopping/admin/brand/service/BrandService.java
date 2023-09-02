package com.shopping.admin.brand.service;

import com.shopping.common.exception.BrandNotFoundException;
import com.shopping.admin.brand.repository.BrandRepository;
import com.shopping.common.entity.Brand;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class BrandService {
    public static int BRAND_PER_PAGE = 8;

    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> listBrands() {
        return (List<Brand>) brandRepository.findAll();
    }

    public Page<Brand> listByPage(Integer pageNum, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, BRAND_PER_PAGE, sort);
        if (keyword != null) {
            return brandRepository.findAll(keyword, pageable);
        }
        return brandRepository.findAll(pageable);
    }

    public String checkUnique(Integer id, String name) {
        boolean isCreate = (id == null || id == 0);

        Brand brandByName = brandRepository.findByName(name);

        if (isCreate) {
            if (brandByName != null) {
                return "DuplicateName";
            }
        } else {
            if (brandByName != null && id != brandByName.getId()) {
                return "DuplicateName";
            }
        }

        return "OK";
    }

    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    public Brand getBrandById(Integer id) throws BrandNotFoundException {
        try {
            return brandRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new BrandNotFoundException("Could not find any brand with ID " + id);
        }
    }

    public void deleteBrand(Integer id) throws BrandNotFoundException {
        Long countById = brandRepository.countById(id);

        if(countById == 0 || countById == null) {
            throw new BrandNotFoundException("Could not find any brand with ID " + id);
        }

        brandRepository.deleteById(id);
    }
}
