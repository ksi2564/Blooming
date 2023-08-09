package com.ssafy.backend.domain.wishlist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.backend.domain.user.User;
import com.ssafy.backend.domain.wishlist.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long>{
//    @Query("select p from Product p join fetch ")
    List<Wishlist> findAllByUser(User user);
    void deleteByProductIdAndUserId(Long productId, Long userId);

}
