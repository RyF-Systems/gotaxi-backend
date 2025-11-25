package com.ryfsystems.ryftaxi.service.impl;


import com.ryfsystems.ryftaxi.model.UserType;
import com.ryfsystems.ryftaxi.repository.UserRepository;
import com.ryfsystems.ryftaxi.repository.UserTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.ryfsystems.ryftaxi.model.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        List<UserType> userTypes = userTypeRepository.findByUserId(user.getId());

        List<String> userTypeNames = userTypes.stream()
                .map(UserType::getTypeName)
                .toList();

        List<GrantedAuthority> authorities = userTypeNames.stream()
                .map(typeName -> new SimpleGrantedAuthority("ROLE_" + typeName.toUpperCase()))
                .collect(Collectors.toList());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
