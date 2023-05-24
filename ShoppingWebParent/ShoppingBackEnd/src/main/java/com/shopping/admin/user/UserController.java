package com.shopping.admin.user;

import com.shopping.admin.FileUploadUtil;
import com.shopping.common.entity.Role;
import com.shopping.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping("/users")
    public String listAll(Model model) {
        List<User> listUsers = service.listUser();
        model.addAttribute("listUsers", listUsers);
        return "users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        List<Role> listRoles = service.listRole();
        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("pageTitle", "Create New User");
        return "user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes,
                           @RequestParam(name = "image") MultipartFile multipartFile) throws IOException {

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);

            User savedUser = service.save(user);

            String uploadDir = "user-photos/" + savedUser.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty()) user.setPhotos(null);
            service.save(user);
        }

        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");

        return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            List<Role> listRoles = service.listRole();
            User user = service.getUserById(id);
            model.addAttribute("user", user);
            model.addAttribute("listRoles", listRoles);
            model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");

            return "user_form";
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("message",
                    "The user ID " + id + " has been deleted successfully");
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable(name = "id") Integer id,
                                          @PathVariable(name = "status") boolean enabled,
                                          RedirectAttributes redirectAttributes) {
        service.updateUserEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The user ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/users";
    }
}
