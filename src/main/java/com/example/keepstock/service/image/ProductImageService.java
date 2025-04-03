package com.example.keepstock.service.image;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ProductImageService {

    String saveProductImage(UUID productId, MultipartFile file);

    byte[] getProductImages(UUID productId);
}