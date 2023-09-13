package com.example.demo.service;

import com.example.demo.entity.SiteUser;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String name, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }

    public Optional<SiteUser> getUserInfo(Integer id) {
        return userRepository.findById(id);
    }

    public Page<SiteUser> getAllUserInfo(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<SiteUser> res = userRepository.findAllByOrderByIdDesc(pageable);
        if (res == null) return null;

        return res;
    }

    public boolean existsPost(Integer id) {
        return userRepository.existsById(id);
    }

    public void accountDelete(Integer id){
        userRepository.deleteById(id);
    }

    public Page<SiteUser> userNameSearchList(String searchKeyword, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<SiteUser> res = userRepository.findByUsernameContainingOrderByIdDesc(searchKeyword, pageable);
        if (res == null) return null;

        return res;
    }

    public Page<SiteUser> nameSearchList(String searchKeyword, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, pageable.getPageSize());

        Page<SiteUser> res = userRepository.findByNameContainingOrderByIdDesc(searchKeyword, pageable);
        if (res == null) return null;

        return res;
    }

}