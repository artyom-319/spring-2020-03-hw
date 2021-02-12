package com.etn319.security;

import com.etn319.dao.mongo.UserMongoRepository;
import com.etn319.model.ServiceUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserMongoRepository repository;

    public UserDetailsServiceImpl(UserMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        ServiceUser user = repository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found:" + username));
        List<GrantedAuthority> authorities = user.getAuthorities().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new User(user.getName(), user.getPass(), authorities);
    }
}
