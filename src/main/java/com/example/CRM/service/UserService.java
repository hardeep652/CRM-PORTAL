package com.example.CRM.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.CRM.model.Users;
import com.example.CRM.repository.UserRepo;

@Service
public class UserService {

    @Autowired
    private UserRepo obj;

    public void saveUser(Users u)
    {
        obj.save(u);
    }
}
