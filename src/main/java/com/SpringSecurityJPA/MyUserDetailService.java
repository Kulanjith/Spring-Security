package com.SpringSecurityJPA;

import com.SpringSecurityJPA.models.MyUserDetails;
import com.SpringSecurityJPA.models.User;
import com.SpringSecurityJPA.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;



@Service
public class MyUserDetailService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyUserDetailService.class);

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        LOGGER.info("request for logging with username [{}]",userName);
        Optional<User> user = userRepository.findByUserName(userName);
        user.orElseThrow(()-> new UsernameNotFoundException("User Not Found " + userName));
        LOGGER.info("user details are taken for user [{}]",userName);
        return  user.map(MyUserDetails::new).get();
    }

}
