package com.tracker.UserService.service.impl;


import com.tracker.UserService.repository.UserRepository;
import com.tracker.UserService.user.JwtToken;
import com.tracker.UserService.user.request.LoginRequest;
import com.tracker.UserService.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserRepository userRepository;

    /**
     * Authenticates the user and generates a JWT token.
     *
     * @param userCredentials the login request containing credentials
     * @return JwtToken containing the generated token
     */
    public JwtToken authenticate(LoginRequest userCredentials) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userCredentials.getIdentifier(),
                        userCredentials.getPassword()));
        String username = authentication.getName();
        log.info("User authenticated successfully with username: {}", username);
        String token = jwtUtil.generateToken(username);
        String id=userRepository.findByEmail(username).get().getId().toString();
        return new JwtToken(token,username,id);

    }

    public void validateToken(String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

}
