package com.example.demo.dto;

import java.util.List;

import lombok.Data;
//「単語一覧から単語をクリックした時に表示する単語詳細」 のデータを取得するための DTO
@Data
public class WordDetailDto {
	private Integer id;
	private String wordName;
	private String content;
	//無限ループ防止のためカテゴリはDTOに変更(変数名はcategoryのまま)
	private CategoryDto category;
	private List<WordDto> relatedWords;
	
}
