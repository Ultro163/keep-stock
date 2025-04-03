package com.example.keepstock.repository;

import com.example.keepstock.entity.ProductsImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProductsImageRepository extends JpaRepository<ProductsImage, UUID> {

    @Query("""
            select pi.imageName
            from ProductsImage pi
            where pi.productId = :productId
            """)
    List<String> findAllNameImagesWithProductId(UUID productId);
}