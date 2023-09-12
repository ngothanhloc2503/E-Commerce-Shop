package com.shopping.admin.customer.controller;

import com.shopping.admin.brand.export.BrandCsvExporter;
import com.shopping.admin.customer.CustomerCsvExporter;
import com.shopping.admin.customer.service.CustomerService;
import com.shopping.admin.paging.PagingAndSortingHelper;
import com.shopping.admin.paging.PagingAndSortingParam;
import com.shopping.admin.setting.repository.CountryRepository;
import com.shopping.admin.setting.repository.StateRepository;
import com.shopping.common.entity.*;
import com.shopping.common.exception.CustomerNotFoundException;
import com.shopping.common.exception.ProductNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateRepository stateRepository;

    @GetMapping("/customers")
    public String listCustomers() {
        return "redirect:/customers/page/1?sortField=id&sortDir=asc";
    }

    @GetMapping("/customers/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "listCustomers", moduleURL = "/customers") PagingAndSortingHelper helper,
            @PathVariable("pageNum") int pageNum) {
        customerService.listByPage(pageNum, helper);

        return "customers/customers";
    }

    @GetMapping("/customers/edit/{customer_id}")
    public String editCustomer(Model model, @PathVariable("customer_id") Integer customerId,
                               RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.findById(customerId);

            List<Country> listCountries = countryRepository.findAllByOrderByNameAsc();
            List<State> listStates = stateRepository.findByCountryOrderByNameAsc(customer.getCountry());

            model.addAttribute("pageTitle", "Edit Customer (" + customerId + "):");
            model.addAttribute("customer", customer);
            model.addAttribute("listCountries", listCountries);
            model.addAttribute("listStates", listStates);

            return "customers/customer_form";
        } catch (CustomerNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/customers";
        }
    }

    @PostMapping("/customers/save")
    private String saveCustomer(Customer customer, RedirectAttributes redirectAttributes) {
        customerService.save(customer);

        redirectAttributes.addFlashAttribute("message", "The customer has been saved successfully!");

        return "redirect:/customers";
    }

    @GetMapping("/customers/delete/{customer_id}")
    private String deleteCustomer(@PathVariable("customer_id") Integer id, RedirectAttributes redirectAttributes) {
        customerService.deleteById(id);

        redirectAttributes.addFlashAttribute("message", "The customer has been saved deleted!");

        return "redirect:/customers";
    }

    @GetMapping("/customers/{id}/enabled/{status}")
    public String updateCustomerEnabledStatus(@PathVariable("id") Integer id,
                                              @PathVariable("status") boolean enabled,
                                              RedirectAttributes redirectAttributes) {
        customerService.updateCustomerEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The customer ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/customers";
    }

    @GetMapping("/customers/export/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        List<Customer> listCustomers = customerService.listAll();
        CustomerCsvExporter customerCsvExporter = new CustomerCsvExporter();
        customerCsvExporter.export(listCustomers, response);
    }

    @GetMapping("/customers/detail/{id}")
    public String viewCustomerDetails(@PathVariable("id") Integer id, Model model,
                                     RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.findById(id);

            model.addAttribute("customer", customer);

            return "customers/customer_details_modal";
        } catch (CustomerNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/customers";
    }
}