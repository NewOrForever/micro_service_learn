package com.example.demo.service;


import com.example.demo.domain.User;

/**
 * @author Fox
 */
public interface IUserService {

    User getByUsername(String username);
}
