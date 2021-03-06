package com.godEShop.Service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import com.godEShop.Dao.AccountDAO;
import com.godEShop.Entity.Account;
import com.godEShop.Service.AccountService;
import com.godEShop.Service.SessionService;

@Service
public class UserDetailImpl implements UserDetailsService {
    @Autowired
    AccountDAO accountDAO;
    @Autowired
    AccountService accountService;

    @Autowired
    SessionService sessionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	// TODO Auto-generated method stub
	sessionService.remove("getUsernameValue");
	try {
	    Account account = accountService.findByUsername(username);
	    // Tạo UserDetails từ Account
	    sessionService.set("getUsernameValue", username.toString());
	    String password = account.getPassword();
	    String role = account.getRole().getId();
	    return User.withUsername(username).password(password).roles(role).build();
	} catch (Exception e) {
	    // TODO: handle exception
	    
	    throw new UsernameNotFoundException(username + "not found");
	}
    }

    public void loginFromOAuth2(OAuth2AuthenticationToken oauth2) {

//		String username = oauth2.getPrincipal().getAttribute("username");
	String email = oauth2.getPrincipal().getAttribute("email");
	// sinh mật khẩu ngẫu nhiên
	String password = Long.toHexString(System.currentTimeMillis());
	// Tạo UserDetails từ Account
	UserDetails user = User.withUsername(email).password(password).roles("Customer").build();

	Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
	// thay thế authentication này bằng authentication từ mạng xh
	SecurityContextHolder.getContext().setAuthentication(auth); 
	// SecurityContextHolder: nơi chứa tt secutiry

    }
}
