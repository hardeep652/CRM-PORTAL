package com.example.CRM.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.CRM.model.Users;
import com.example.CRM.service.UserService;

@RestController
@RequestMapping("/api/employees")
public class UserController {

    @Autowired
    private UserService  obj;
    
    @PostMapping("/newEmployee")
    public String addemployee(@RequestBody Users u){
        obj.saveUser(u);
        return "the employee was added:"+u;
    }
}
