package com.example.demo.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;

/*
 * -- 単語
CREATE TABLE word (
    id INT AUTO_INCREMENT PRIMARY KEY,
    word_name VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    category_id INT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES category(id),
    UNIQUE(category_id,word_name)
);
INSERT INTO word(word_name, content, category_id)
VALUES("word1", "word1", 1);

INSERT INTO word(word_name, content, category_id)
VALUES("word2", "word2_content", 1);

INSERT INTO word(word_name, content, category_id)
VALUES("word3", "word3_content", 2);

INSERT INTO word(word_name, content, category_id)
VALUES("word4", "word4_content", 2);

 */
@Entity
@Table(name="word")
@Data
 //JSON変換時にword_relationを介した循環参照を防ぐ
 //同じidのオブジェクトが2回目に出てきたら、idのみを参照する
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id"
		)
public class Word {
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="word_name")
	private String wordName;
	
	@Column(name="content")
	private String content;
	
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @ManyToOne
    @JoinColumn(name="wordbook_id",nullable = false)
    private Wordbook wordbook;
    
    //word_relationテーブルとのマッピング
    @ManyToMany
    @JoinTable(
        name = "word_relation",
        joinColumns = @JoinColumn(name = "word_id"),
        inverseJoinColumns = @JoinColumn(name = "related_word_id")
    )
    private List<Word> relatedWords;
    
    
    
    
}
