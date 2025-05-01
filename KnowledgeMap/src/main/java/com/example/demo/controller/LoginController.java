package com.example.demo.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.WordBook;
import com.example.demo.service.LoginUserDetails;
import com.example.demo.service.UserService;
import com.example.demo.service.WordBookService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class LoginController {
	private final UserService userService;
	private final WordBookService wordBookService;
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/home")
	public String home(
			Model model,
			@AuthenticationPrincipal LoginUserDetails loginUserDetails) {
		List<WordBook> wordBookList = wordBookService.findWordBook(loginUserDetails.getUser());
		model.addAttribute("wordBookList", wordBookList);
	    return "home";
	}

}
