package org.kt.hw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.kt.hw.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
//    List<User> findAll();

//    User findById(int id);
}
