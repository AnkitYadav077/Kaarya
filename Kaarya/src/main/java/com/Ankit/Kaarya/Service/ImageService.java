package com.Ankit.Kaarya.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.Ankit.Kaarya.Exceptions.ImageProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto")
            );
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new ImageProcessingException("Failed to upload image: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ImageProcessingException("Unexpected error during image upload: " + e.getMessage(), e);
        }
    }
}