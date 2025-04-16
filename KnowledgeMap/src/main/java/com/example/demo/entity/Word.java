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

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/*
 * +----+---------+-------------------------------------------------------------------+-------------+
| id | word    | content                                                              | category_id |
+----+---------+----------------------------------------------------------------------+-------------+
|  1 | API     | 異なるシステムやアプリケーションがデータや機能をやり取りするための仕組み          |           1 |
 */
@Entity
@Table(name="word")
@Data
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

    @ManyToMany
    @JsonIgnore
    @JoinTable(
        name = "word_relation",
        joinColumns = @JoinColumn(name = "word_id"),
        inverseJoinColumns = @JoinColumn(name = "related_word_id")
    )
    private List<Word> relatedWords;
}
