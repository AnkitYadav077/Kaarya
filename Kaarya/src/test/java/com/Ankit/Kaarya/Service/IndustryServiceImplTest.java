package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.Industry;
import com.Ankit.Kaarya.Entity.Jobs;
import com.Ankit.Kaarya.Exceptions.ResourceNotFoundException;
import com.Ankit.Kaarya.Payloads.IndustryDto;
import com.Ankit.Kaarya.Repo.IndustryRepo;
import com.Ankit.Kaarya.Repo.JobApplicationRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndustryServiceImplTest {

    @Mock
    private IndustryRepo industryRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ImageService imageService;

    @Mock
    private JobApplicationRepo jobApplicationRepo;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private IndustryServiceImpl industryService;

    @Test
    void registerIndustry_Success() {
        // Arrange
        IndustryDto industryDto = new IndustryDto();
        Industry industry = new Industry();
        Industry savedIndustry = new Industry();

        when(modelMapper.map(industryDto, Industry.class)).thenReturn(industry);
        when(industryRepo.save(industry)).thenReturn(savedIndustry);
        when(modelMapper.map(savedIndustry, IndustryDto.class)).thenReturn(industryDto);

        // Act
        IndustryDto result = industryService.registerIndustry(industryDto, "INDUSTRY");

        // Assert
        assertNotNull(result);
        verify(industryRepo).save(industry);
    }

    @Test
    void getProfile_Exists_ReturnsIndustryDto() {
        // Arrange
        Integer industryId = 1;
        Industry industry = new Industry();
        IndustryDto dto = new IndustryDto();

        when(industryRepo.findById(industryId)).thenReturn(Optional.of(industry));
        when(modelMapper.map(industry, IndustryDto.class)).thenReturn(dto);

        // Act
        IndustryDto result = industryService.getProfile(industryId);

        // Assert
        assertNotNull(result);
        verify(industryRepo).findById(industryId);
    }

    @Test
    void getProfile_NotFound_ThrowsException() {
        // Arrange
        Integer industryId = 1;
        when(industryRepo.findById(industryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            industryService.getProfile(industryId);
        });
    }

    @Test
    void updateIndustry_Success() {
        // Arrange
        Integer industryId = 1;
        IndustryDto industryDto = new IndustryDto();
        industryDto.setName("New Name");
        industryDto.setEmail("new@email.com");

        Industry industry = new Industry();
        Industry updatedIndustry = new Industry();
        IndustryDto resultDto = new IndustryDto();

        when(industryRepo.findById(industryId)).thenReturn(Optional.of(industry));
        when(industryRepo.save(any())).thenReturn(updatedIndustry);
        when(modelMapper.map(updatedIndustry, IndustryDto.class)).thenReturn(resultDto);

        // Act
        IndustryDto result = industryService.updateIndustry(industryId, industryDto);

        // Assert
        assertNotNull(result);
        verify(industryRepo).save(any(Industry.class));
    }

    @Test
    void uploadIndustryImage_Success() throws IOException {
        // Arrange
        Long industryId = 1L;
        Industry industry = new Industry();
        IndustryDto dto = new IndustryDto();
        String imageUrl = "http://example.com/image.jpg";

        when(industryRepo.findById(anyInt())).thenReturn(Optional.of(industry));
        when(imageService.uploadImage(any())).thenReturn(imageUrl);
        when(industryRepo.save(any())).thenReturn(industry);
        when(modelMapper.map(industry, IndustryDto.class)).thenReturn(dto);

        // Act
        IndustryDto result = industryService.uploadIndustryImage(industryId, multipartFile);

        // Assert
        assertNotNull(result);
        assertEquals(imageUrl, industry.getImageUrl());
        verify(imageService).uploadImage(multipartFile);
    }
}