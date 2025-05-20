package com.example.demo.advice;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.exception.DeleteFailException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnexpectedException;

@ControllerAdvice
public class CommonExceptionHandler {
	
	@ExceptionHandler(UnexpectedException.class)
	public String errorHandling(UnexpectedException e, Model model) {
		model.addAttribute("errorMsg", e.getMessage());
		return "unexpected_error";
	}
	
	// リクエストで指定されたidが見つからない場合
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<?> resourceNotFoundExceptionHandling(ResourceNotFoundException e){
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("error",e.getMessage()));
	}
	
	// リクエストは妥当だが削除処理中に何らかのエラーが発生した場合
	@ExceptionHandler(DeleteFailException.class)
	public ResponseEntity<?> deleteFailExceptionHandling(DeleteFailException e){
		e.printStackTrace();
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(Map.of("error",e.getMessage()));
	}
}
