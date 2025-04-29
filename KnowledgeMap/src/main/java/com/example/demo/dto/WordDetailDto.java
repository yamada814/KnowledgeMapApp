package com.example.demo.dto;

import java.util.List;

import com.example.demo.entity.Category;

import lombok.Data;
//「単語一覧から単語をクリックした時に表示する単語詳細」 のデータを取得するための DTO
@Data
public class WordDetailDto {
	private Integer id;
	private String wordName;
	private String content;
	private Category category;
	private List<WordDto> relatedWords;
	
}
