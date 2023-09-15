package com.shopping.admin.shipping_rate.controller;

import com.shopping.admin.paging.PagingAndSortingHelper;
import com.shopping.admin.paging.PagingAndSortingParam;
import com.shopping.admin.setting.repository.CountryRepository;
import com.shopping.admin.shipping_rate.exception.ShippingRateAlreadyExistException;
import com.shopping.admin.shipping_rate.exception.ShippingRateNotFoundException;
import com.shopping.admin.shipping_rate.service.ShippingRateService;
import com.shopping.common.entity.Country;
import com.shopping.common.entity.ShippingRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ShippingRateController {
    @Autowired
    private ShippingRateService shippingRateService;
    @Autowired
    private CountryRepository countryRepository;

    @GetMapping("/shipping_rates")
    public String listFirstPage() {
        return "redirect:/shipping_rates/page/1?sortField=id&sortDir=asc";
    }

    @GetMapping("/shipping_rates/page/{pageNum}")
    public String listByPage(@PathVariable("pageNum") int pageNum,
                             @PagingAndSortingParam(moduleURL = "/shipping_rates", listName = "listShippingRates")PagingAndSortingHelper helper) {
        shippingRateService.listByPage(pageNum, helper);
        return "shipping_rates/shipping_rates";
    }

    @GetMapping("/shipping_rates/new")
    public String newShippingRate(Model model) {
        ShippingRate shippingRate = new ShippingRate();
        List<Country> listCountries = countryRepository.findAllByOrderByNameAsc();

        model.addAttribute("shippingRate", shippingRate);
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "New Shipping Rate");

        return "shipping_rates/shipping_rate_form";
    }

    @PostMapping("/shipping_rates/save")
    public String saveShippingRate(ShippingRate shippingRate,
                                   RedirectAttributes redirectAttributes) {
        try {
            shippingRateService.save(shippingRate);
            redirectAttributes.addFlashAttribute("message", "The shipping rate has been saved successfully!");
        } catch (ShippingRateAlreadyExistException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/shipping_rates/page/1?sortField=id&sortDir=asc";
    }

    @GetMapping("/shipping_rates/edit/{rate_id}")
    public String editShippingRate(@PathVariable("rate_id") Integer shippingRateId, Model model,
                                   RedirectAttributes redirectAttributes) {
        try {
            ShippingRate shippingRate = shippingRateService.get(shippingRateId);
            List<Country> listCountries = countryRepository.findAllByOrderByNameAsc();

            model.addAttribute("shippingRate", shippingRate);
            model.addAttribute("listCountries", listCountries);
            model.addAttribute("pageTitle", "Edit Shipping Rate (" + shippingRateId + ")");

            return "shipping_rates/shipping_rate_form";
        } catch (ShippingRateNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/shipping_rates/page/1?sortField=id&sortDir=asc";
    }

    @GetMapping("/shipping_rates/cod/{id}/enabled/{supported}")
    public String updateCODSupport(@PathVariable("id") Integer id, @PathVariable("supported") boolean supported,
                                   Model model, RedirectAttributes redirectAttributes) {
        try {
            shippingRateService.updateCODSupport(id, supported);

            redirectAttributes.addFlashAttribute("message",
                    "Cod support for shipping rate ID " + id + " has been updated successfully");
        } catch (ShippingRateNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/shipping_rates/page/1?sortField=id&sortDir=asc";
    }

    @GetMapping("/shipping_rates/delete/{rate_id}")
    public String deleteShippingRate(@PathVariable(name = "rate_id") Integer id,
                             RedirectAttributes redirectAttributes) {
        try {
            shippingRateService.delete(id);

            redirectAttributes.addFlashAttribute("message",
                    "The shipping rate ID " + id + " has been deleted successfully");
        } catch (ShippingRateNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/shipping_rates/page/1?sortField=id&sortDir=asc";
    }
}
