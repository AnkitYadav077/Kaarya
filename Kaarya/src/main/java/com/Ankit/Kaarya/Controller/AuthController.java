package com.Ankit.Kaarya.Controller;

import com.Ankit.Kaarya.Payloads.JwtAuthResponse;
import com.Ankit.Kaarya.Security.JwtUtil;
import com.Ankit.Kaarya.Security.OtpAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/otp/verify")
    public JwtAuthResponse loginWithOtp(@RequestParam String otp, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("IDENTITY_KEY") == null) {
            throw new RuntimeException("Session expired or phone/email not found.");
        }

        String key = session.getAttribute("IDENTITY_KEY").toString();
        OtpAuthenticationToken authToken = new OtpAuthenticationToken(key, otp);
        Authentication authResult = authenticationManager.authenticate(authToken);

        OtpAuthenticationToken authenticatedToken = (OtpAuthenticationToken) authResult;
        String username = authenticatedToken.getPrincipal().toString();
        String role = authenticatedToken.getAuthorities().iterator().next().getAuthority();
        Long id = authenticatedToken.getId();

        session.invalidate();

        String token = jwtUtil.generateToken(username, role, id);
        return new JwtAuthResponse(token);
    }
}