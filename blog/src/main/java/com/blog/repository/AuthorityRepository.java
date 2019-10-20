package com.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blog.model.Authority;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {

}
