package com.example.demo.springpagination.service;

import com.example.demo.springpagination.model.User;

public interface IUserService {

    User getCurrentUser();

	User createUser(User user);

	User getUserById(Long id);
	
	void updateUser(User user);
	
	void deleteUser(Long id);

}