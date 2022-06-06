package com.godEShop.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.godEShop.Entity.User;

@Repository
public interface UserDAO extends JpaRepository<User, Integer> {
    @Query(value = "SELECT * FROM Users WHERE username LIKE ?1", nativeQuery = true)
    User findByUsername(String username);
    
    @Query("SELECT u FROM User u WHERE u.account.username=?1")
    User findByAccountUsername(String username);
}
