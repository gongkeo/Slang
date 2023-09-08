package com.example.demo.springpagination.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.springpagination.model.Post;
import com.example.demo.springpagination.model.User;

public interface PostRepository extends JpaRepository<Post, Long> {
	
	@Query("select u from Post u where u.userName=:userName")
    Page<Post> findByUser(@Param("userName") String userName, Pageable pageReq);

    default Page<Post> findByUser(User user, Pageable pageReq) {
        return findByUser(user.getUser_id(), pageReq);
    }

}
