package com.Ankit.Kaarya.Security;

import com.Ankit.Kaarya.Entity.Industry;
import com.Ankit.Kaarya.Entity.Users;
import com.Ankit.Kaarya.Repo.IndustryRepo;
import com.Ankit.Kaarya.Repo.UserRepo;
import com.Ankit.Kaarya.Service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OtpAuthenticationProvider implements AuthenticationProvider {

    private final OtpService otpService;
    private final UserRepo userRepo;
    private final IndustryRepo industryRepo;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String principal = authentication.getPrincipal().toString().toLowerCase().replace("+91", "").trim();
        String otp = authentication.getCredentials().toString();

        boolean valid = otpService.validateOtp(principal, otp);
        if (!valid) throw new RuntimeException("Invalid OTP");

        if (principal.contains("@")) {
            Industry industry = industryRepo.findByEmail(principal)
                    .orElseThrow(() -> new RuntimeException("Industry not found"));
            return new OtpAuthenticationToken(
                    industry.getIndustryId(),
                    industry.getEmail(),
                    null,
                    "ROLE_INDUSTRY",
                    List.of(new SimpleGrantedAuthority("ROLE_INDUSTRY"))
            );
        } else {
            Users user = userRepo.findByPhoneNo(principal)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return new OtpAuthenticationToken(
                    user.getUserId(),
                    user.getPhoneNo(),
                    null,
                    "ROLE_USER",
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return OtpAuthenticationToken.class.isAssignableFrom(authentication);
    }
}