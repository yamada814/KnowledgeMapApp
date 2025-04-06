package com.example.demo.form;

import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class WordForm {
	@NotBlank(message="wordが空です")
    private String wordName;
	@NotBlank(message="contentが空です")
    private String content;
	//@NotNull(message="categoryが選択されていません")
    private Integer categoryId;                // カテゴリ選択用
	
	private String categoryName;
    private List<Long> relatedWordIds;      // 関連語（複数選択）
    
    @AssertTrue(message="categoryが選択されていません")
    public boolean isCategoryNotNull() {
    	return categoryId != null || (categoryName != null && !categoryName.isBlank());
    }
}
