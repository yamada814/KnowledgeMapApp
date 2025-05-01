package com.example.demo.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.entity.Wordbook;
import com.example.demo.service.LoginUserDetails;
import com.example.demo.service.UserService;
import com.example.demo.service.WordbookService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class LoginController {
	private final UserService userService;
	private final WordbookService wordbookService;
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/wordbooks")
	public String home(
			Model model,
			@AuthenticationPrincipal LoginUserDetails loginUserDetails) {
		List<Wordbook> wordbookList = wordbookService.findWordBook(loginUserDetails.getUser());
		model.addAttribute("wordbookList", wordbookList);
	    return "wordbook_list";
	}

}
