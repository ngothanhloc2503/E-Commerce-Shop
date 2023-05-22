package com.shopping.admin.user;

import com.shopping.common.entity.Role;
import com.shopping.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired RoleRepository roleRepository;

    public List<User> listUser() {
        return (List<User>) userRepository.findAll();
    }

    public List<Role> listRole() {
        return (List<Role>) roleRepository.findAll();
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
