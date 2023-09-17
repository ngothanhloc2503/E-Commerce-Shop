package com.shopping.address;

import com.shopping.Utility;
import com.shopping.common.entity.Address;
import com.shopping.common.entity.Country;
import com.shopping.common.entity.Customer;
import com.shopping.customer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AddressController {
    @Autowired
    private AddressService addressService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/address_book")
    public String showAddressBook(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        List<Address> listAddresses = addressService.listAddressBook(customer);

        boolean primaryAddressAsDefault = true;
        for (Address address : listAddresses) {
            if (address.isDefaultForShipping()) {
                primaryAddressAsDefault = false;
                break;
            }
        }

        model.addAttribute("listAddresses", listAddresses);
        model.addAttribute("customer", customer);
        model.addAttribute("usePrimaryAddressAsDefault", primaryAddressAsDefault);

        return "address_book/addresses";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request){
        String customerEmail = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(customerEmail);
    }

    @GetMapping("/address_book/new")
    public String showNewAddressForm(Model model) {
        List<Country> listCountries = customerService.listAllCountries();

        model.addAttribute("pageTitle", "Add New Address");
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("address", new Address());

        return "address_book/address_form";
    }

    @PostMapping("/address_book/save")
    public String saveAddress(Address address, HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {
        address.setCustomer(getAuthenticatedCustomer(request));

        addressService.save(address);

        redirectAttributes.addFlashAttribute("message", "The address has been saved successfully.");

        return "redirect:/address_book";
    }

    @GetMapping("/address_book/edit/{id}")
    public String editAddress(@PathVariable("id") Integer id, HttpServletRequest request, Model model) {
        Customer customer = getAuthenticatedCustomer(request);

        Address address = addressService.get(id, customer.getId());

        List<Country> listCountries = customerService.listAllCountries();

        model.addAttribute("pageTitle", "Add New Address");
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("address", address);

        return "address_book/address_form";
    }

    @GetMapping("/address_book/delete/{id}")
    public String deleteAddress(@PathVariable("id") Integer id, HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        Customer customer = getAuthenticatedCustomer(request);

        addressService.delete(id, customer.getId());

        redirectAttributes.addFlashAttribute("message", "The address ID " + id + " has been deleted.");

        return "redirect:/address_book";
    }

    @GetMapping("/address_book/default/{id}")
    public String setDefaultAddress(@PathVariable("id") Integer id, HttpServletRequest request) {
        addressService.setDefault(id, getAuthenticatedCustomer(request).getId());

        return "redirect:/address_book";
    }
}
