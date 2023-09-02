package com.shopping.product;

import com.shopping.category.CategoryService;
import com.shopping.common.entity.Category;
import com.shopping.common.entity.Product;
import com.shopping.common.exception.CategoryNotFoundException;
import com.shopping.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/c/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable("category_alias") String alias, Model model) {
        return viewCategoryByPage(alias, model, 1);
    }

    @GetMapping("/c/{category_alias}/page/{pageNum}")
    public String viewCategoryByPage(@PathVariable("category_alias") String alias, Model model,
                                     @PathVariable("pageNum") int pageNum) {
        try {
            Category category = categoryService.getCategoryByAliasEnabled(alias);
            List<Category> listCategoryParents = categoryService.getCategoryParents(category);

            Page<Product> pageProducts = productService.listByCategory(pageNum, category.getId());
            List<Product> listProducts = pageProducts.getContent();

            long startCount = (pageNum - 1) * productService.PRODUCTS_PER_PAGE + 1;
            long endCount = startCount + productService.PRODUCTS_PER_PAGE - 1;

            if (endCount > pageProducts.getTotalElements()) {
                endCount = pageProducts.getTotalElements();
            }

            model.addAttribute("currentPage", pageNum);
            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("totalItems", pageProducts.getTotalElements());
            model.addAttribute("totalPages", pageProducts.getTotalPages());
            model.addAttribute("pageTitle", category.getName());
            model.addAttribute("category", category);
            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("listProducts", listProducts);

            return "product/products_by_category";
        } catch (CategoryNotFoundException e) {
            return "error/404";
        }
    }

    @GetMapping("/p/{product_alias}")
    public String viewProductDetail(@PathVariable("product_alias") String alias, Model model) {
        try {
            Product product = productService.getProduct(alias);
            List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());

            model.addAttribute("pageTitle", product.getShortName());
            model.addAttribute("product", product);
            model.addAttribute("listCategoryParents", listCategoryParents);

            return "product/product_detail";
        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }

    @GetMapping("/search")
    public String searchFirstPage(@Param("keyword") String keyword, Model model) {
        return searchByPage(keyword, model, 1);
    }

    @GetMapping("/search/page/{pageNum}")
    public String searchByPage(@Param("keyword") String keyword, Model model,
                               @PathVariable("pageNum") int pageNum) {
        Page<Product> pageProducts = productService.search(keyword, pageNum);
        List<Product> listResult = pageProducts.getContent();

        long startCount = (pageNum - 1) * productService.SEARCH_RESULT_PER_PAGE + 1;
        long endCount = startCount + productService.SEARCH_RESULT_PER_PAGE - 1;

        if (endCount > pageProducts.getTotalElements()) {
            endCount = pageProducts.getTotalElements();
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", pageProducts.getTotalElements());
        model.addAttribute("totalPages", pageProducts.getTotalPages());
        model.addAttribute("pageTitle", keyword + " - Search Result");
        model.addAttribute("keyword", keyword);
        model.addAttribute("listResult", listResult);

        return "product/search_result";
    }
}
