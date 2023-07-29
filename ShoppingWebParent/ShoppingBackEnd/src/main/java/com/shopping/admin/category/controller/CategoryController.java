package com.shopping.admin.category.controller;

import com.shopping.admin.FileUploadUtil;
import com.shopping.admin.category.CategoryNotFoundException;
import com.shopping.admin.category.CategoryPageInfo;
import com.shopping.admin.category.service.CategoryService;
import com.shopping.admin.user.UserNotFoundException;
import com.shopping.common.entity.Category;
import com.shopping.common.entity.Role;
import com.shopping.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String listFirstPage(@Param("sortDir") String sortDir, Model model) {
        return listByPage(1, sortDir, null, model);
    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(@PathVariable("pageNum") int pageNum, @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword, Model model) {
        if (sortDir == null || sortDir.isEmpty()) {
            sortDir = "asc";
        }

        CategoryPageInfo categoryPageInfo = new CategoryPageInfo();

        List<Category> categoryList = categoryService.listByPage(categoryPageInfo, pageNum, sortDir, keyword);

        long startCount = (pageNum - 1) * categoryService.ROOT_CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + categoryService.ROOT_CATEGORIES_PER_PAGE - 1;

        if (endCount > categoryPageInfo.getTotalElements()) {
            endCount = categoryPageInfo.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("totalPages", categoryPageInfo.getTotalPages());
        model.addAttribute("totalItems", categoryPageInfo.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("sortField", "name");
        model.addAttribute("sortDir", "name");
        model.addAttribute("keyword", keyword);
        model.addAttribute("listCategories", categoryList);
        model.addAttribute("reverseSortDir", reverseSortDir);

        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        List<Category> categoryList = categoryService.listCategoriesUsedInForm();
        model.addAttribute("listCategories", categoryList);
        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Create New Category");
        return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category, @RequestParam("fileImage") MultipartFile multipartFile,
                               RedirectAttributes redirectAttributes)
            throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            category.setImage(fileName);

            Category savedCategory = categoryService.save(category);

            String uploadDir = "../category-images/" + savedCategory.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            categoryService.save(category);
        }

        redirectAttributes.addFlashAttribute("message", "The category has been saved successfully!");

        return "redirect:/categories";

    }

    @GetMapping("categories/edit/{id}")
    public String editCategory(@PathVariable("id") Integer id, Model model,
                               RedirectAttributes redirectAttributes) throws CategoryNotFoundException {
        try {
            List<Category> listCategories = categoryService.listCategoriesUsedInForm();
            Category category = categoryService.getCategoryById(id);
            model.addAttribute("category", category);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Edit Category (ID: " + id + ")");

            return "categories/category_form";
        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/categories";
        }

    }

    @GetMapping("categories/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
                                              RedirectAttributes redirectAttributes) {
        categoryService.updateCategoryEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The category ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/categories";
    }

    @GetMapping("categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            String categoryDir = "../category-images/" + id;
            FileUploadUtil.removeDir(categoryDir);

            redirectAttributes.addFlashAttribute("message",
                    "The category ID " + id + " has been deleted successfully");
        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/categories";
    }
}
