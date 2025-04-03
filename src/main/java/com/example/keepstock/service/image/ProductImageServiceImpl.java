package com.example.keepstock.service.image;

import com.example.keepstock.entity.ProductsImage;
import com.example.keepstock.error.exception.EntityNotFoundException;
import com.example.keepstock.error.exception.ImageDownloadException;
import com.example.keepstock.error.exception.ImageNotFoundException;
import com.example.keepstock.error.exception.ImageUploadException;
import com.example.keepstock.repository.ProductRepository;
import com.example.keepstock.repository.ProductsImageRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final MinioClient minioClient;
    private final ProductsImageRepository productsImageRepository;
    private final ProductRepository productRepository;

    @Value("${s3.bucket}")
    private String bucketName;

    public String saveProductImage(UUID productId, MultipartFile file) {
        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("Product with ID=%s not found".formatted(productId));
        }
        uploadImageForProduct(file);
        ProductsImage productImage = new ProductsImage();
        productImage.setProductId(productId);
        productImage.setImageName(file.getOriginalFilename());
        productsImageRepository.save(productImage);
        return productImage.getImageName();
    }

    public byte[] getProductImages(UUID productId) {
        List<String> imageNames = productsImageRepository.findAllNameImagesWithProductId(productId);
        if (imageNames == null || imageNames.isEmpty()) {
            throw new ImageNotFoundException("Product with ID=%s not have image".formatted(productId));
        }
        return downloadProductImagesAsZip(imageNames);
    }

    private void uploadImageForProduct(MultipartFile file) {

        try (InputStream fileStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(file.getOriginalFilename())
                            .stream(fileStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            file.getOriginalFilename();
        } catch (Exception e) {
            throw new ImageUploadException("File upload error in S3");
        }
    }

    private byte[] downloadProductImagesAsZip(List<String> imageNames) {

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
                for (String imageName : imageNames) {
                    InputStream fileStream = minioClient.getObject(
                            GetObjectArgs.builder().bucket(bucketName).object(imageName).build()
                    );

                    ZipEntry zipEntry = new ZipEntry(imageName);
                    zipOutputStream.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fileStream.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, length);
                    }
                    zipOutputStream.closeEntry();
                    fileStream.close();
                }
            }
            return byteArrayOutputStream.toByteArray();

        } catch (Exception e) {
            throw new ImageDownloadException("Error when downloading product images from S3 to a ZIP file");
        }
    }
}