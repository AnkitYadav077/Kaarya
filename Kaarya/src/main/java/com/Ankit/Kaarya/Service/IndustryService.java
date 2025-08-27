package com.Ankit.Kaarya.Service;

import com.Ankit.Kaarya.Entity.Industry;
import com.Ankit.Kaarya.Payloads.IndustryDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface IndustryService {

    IndustryDto registerIndustry(IndustryDto industry,String role);

    IndustryDto getProfile(Integer industryId);

    IndustryDto updateIndustry(Integer industryId, IndustryDto industry);

    IndustryDto getUsersAppliedForEachJobByIndustry(Long industryId);

    IndustryDto uploadIndustryImage(Long industryId, MultipartFile file) throws IOException;

}