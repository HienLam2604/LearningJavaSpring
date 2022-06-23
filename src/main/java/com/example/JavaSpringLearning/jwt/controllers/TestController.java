package com.example.JavaSpringLearning.jwt.controllers;

import com.example.JavaSpringLearning.jwt.models.AuthenticationResponse;
import com.example.JavaSpringLearning.jwt.models.UserModel;
import com.example.JavaSpringLearning.jwt.repository.UserRepository;
import com.example.JavaSpringLearning.jwt.services.UserDetailsImpl;
import com.example.JavaSpringLearning.jwt.services.UserDetailsServiceImpl;
import com.example.JavaSpringLearning.jwt.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@CrossOrigin(origins ="http://localhost:4200")
public class TestController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserDetailsServiceImpl myUserDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @GetMapping("/public")
    public String publicSite(){
        return "Public site!";
    }
    @GetMapping("/user")
    public String userSite(){
        return "USER!";
    }
    @GetMapping("/admin")
    public List<UserModel> adminSite(){
        return userRepository.findAll();
    }


    @RequestMapping(value = "/auth",method = RequestMethod.POST)
    public ResponseEntity<?> authenticateUser(@RequestBody UserModel loginRequest) throws Exception{
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword())
            );
        }catch (BadCredentialsException e){
            throw new Exception("Incorrect username or password !");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

         return ResponseEntity.ok(new AuthenticationResponse(jwt,loginRequest.getUsername(),roles)); // return jwt

    }
}
