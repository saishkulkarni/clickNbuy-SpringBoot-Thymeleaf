package com.m15.clicknbuy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.m15.clicknbuy.entity.User;
import com.m15.clicknbuy.repository.UserRepository;

@Component
public class CustomUserDetailService implements UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("No User Found"));

		if (user.isVerified())
			return new CustomUserDetails(user.getEmail(), user.getPassword(), user.getRole());
		else
			throw new UsernameNotFoundException("No User Found");
	}

}
