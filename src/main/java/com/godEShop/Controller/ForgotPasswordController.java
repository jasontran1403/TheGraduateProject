package com.godEShop.Controller;

import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.godEShop.Entity.Account;
import com.godEShop.Entity.MailInfo;
import com.godEShop.Entity.User;
import com.godEShop.Service.AccountService;
import com.godEShop.Service.MailerService;
import com.godEShop.Service.UserService;

@Controller
public class ForgotPasswordController {
	@Autowired
	AccountService accountService;
	@Autowired
	UserService userService;
	@Autowired
	MailerService mailerServie;

	@Autowired
	ServletContext application;

	@Autowired
	HttpServletRequest req;
	
	@Autowired
	BCryptPasswordEncoder pe;
	static Boolean isVerificationEmail = false;
	@GetMapping("/forgotPassword")
	public String layout() {
		return "account/forgot-password";
	}
	int checkPinNumber=0;
	Account getAccount;
	@RequestMapping("/forgotPassword-send")
    public String sendMail(Model model,@RequestParam("username") String username ,@RequestParam("email") String email) {
		getAccount = accountService.findByUsername(username);
		User u = userService.findByAccountUsername(username);
		try {
			if(email.equalsIgnoreCase(u.getEmail()) ) {
				String randomPIN ="";
				Random rdn = new Random();
				int number = rdn.nextInt(999999);
				randomPIN="" + number;
				if(number < 999999) {
					randomPIN = "0" + number;
				}
				checkPinNumber= Integer.parseInt(randomPIN);
				MailInfo m = new MailInfo();
				m.setFrom("testemailnghiatran@gmail.com");
				m.setSubject("Reset login password GodEShop ");
				m.setTo(email);
				m.setBody("OTP: "+checkPinNumber);
				try {
				    mailerServie.send(m);
				} catch (Exception e) {
				    System.out.println("Error : " + e.getMessage());
				}
				return "/account/verification";
			}else {
				model.addAttribute("message","Email không hợp lệ");
				return "account/forgot-password";
			}
		} catch (Exception e) {
			// TODO: handle exception
			model.addAttribute("message","Tài khoản không hợp lệ.");
			return "account/forgot-password";
		}
	

	
	
    }
	Account acceptAccount = null;
	@RequestMapping("/checkPin")
	public String checkPin(Model model, @RequestParam("number") String number) {
	
		try {
			int pin = Integer.parseInt(number);
			if(pin==checkPinNumber) {
				model.addAttribute("isVerificationEmail", "true");
				isVerificationEmail = true;
				 return  "/account/changePasswordForm";
			}else {
				model.addAttribute("message","invalid verification code");
				return "/account/verification";
			}
		} catch (Exception e) {
			model.addAttribute("message","invalid verification code");
			return "/account/verification";
		}
	
	}
	  @PostMapping("/changePasswordForgot")
	  public String changePassword(HttpServletRequest request,Model model,@RequestParam("newPassword") String newPassword
			  ,@RequestParam("confirmPassword") String confirmPassword) {
		  try {
			  Account acc = accountService.findByUsername(getAccount.getUsername()); 
			if(isVerificationEmail == true && newPassword.equals(confirmPassword) ) {		
				acc.setPassword(pe.encode(newPassword)); 
				accountService.update(acc);		
				return "/account/login";
			}
		} catch (Exception e) {
			// TODO: handle exception	
			  return "redirect:/changePasswordForm";
		}
			return "/account/login";
	  }
	
}
