package com.example.demo.dto;

import lombok.Data;
// 「カテゴリをクリックしたときに表示する単語一覧」 のデータを取得するための 単語DTO
@Data
public class WordDto {
	    private Integer id;
	    private String wordName;
	    private String content;
	    private Integer categoryId;
	
}
