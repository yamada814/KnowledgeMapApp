package com.example.demo.form;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class WordbookForm {
	@NotBlank(message="単語帳名が空です")
	private String wordbookName;
	private Integer userId;

}
