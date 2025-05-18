package com.example.demo.advice;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.exception.UnexpectedException;

@ControllerAdvice
public class CommonExceptionHandler {
	
	@ExceptionHandler(UnexpectedException.class)
	//UnexpectedException型のエラーを感知するとこのメソッドが呼び出される
	public String errorHandling(UnexpectedException e, Model model) {
		model.addAttribute("errorMsg", e.getMessage());
		return "unexpeted_error.html";
	}
}
