package com.Ankit.Kaarya.Controller;

import com.Ankit.Kaarya.Payloads.IndustryDto;
import com.Ankit.Kaarya.Security.OtpAuthenticationToken;
import com.Ankit.Kaarya.Service.IndustryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/industry")
@RequiredArgsConstructor
public class IndustryController {

    private final IndustryService industryService;


    private Long getAuthenticatedIndustryId() {
        OtpAuthenticationToken auth =
                (OtpAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return auth.getId();
    }

    @PostMapping("/register")
    public ResponseEntity<IndustryDto> registerIndustry(@Valid @RequestBody IndustryDto industryDto) {
        IndustryDto createdIndustry = industryService.registerIndustry(industryDto, "ROLE_INDUSTRY");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIndustry);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('ROLE_INDUSTRY')")
    public ResponseEntity<IndustryDto> getProfileById() {
        Long industryId = getAuthenticatedIndustryId();
        IndustryDto industryProfile = industryService.getProfile(industryId.intValue());
        return ResponseEntity.ok(industryProfile);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_INDUSTRY')")
    public ResponseEntity<IndustryDto> updateIndustry(@RequestBody IndustryDto industryDto) {
        Long industryId = getAuthenticatedIndustryId();
        IndustryDto updatedIndustry = industryService.updateIndustry(industryId.intValue(), industryDto);
        return ResponseEntity.ok(updatedIndustry);
    }

    @GetMapping("/applicants")
    @PreAuthorize("hasAuthority('ROLE_INDUSTRY')")
    public ResponseEntity<IndustryDto> getApplicantsForJobs() {
        Long industryId = getAuthenticatedIndustryId();
        IndustryDto response = industryService.getUsersAppliedForEachJobByIndustry(industryId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload-image")
    @PreAuthorize("hasAuthority('ROLE_INDUSTRY')")
    public ResponseEntity<IndustryDto> uploadIndustryImage(
            @RequestParam("image") MultipartFile file) throws IOException {
        Long industryId = getAuthenticatedIndustryId();
        IndustryDto updatedIndustry = industryService.uploadIndustryImage(industryId, file);
        return ResponseEntity.ok(updatedIndustry);
    }
}