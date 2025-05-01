package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;

/*
 * -- 関連語リンクテーブル（自己結合・多対多）
CREATE TABLE word_relation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    word_id INT NOT NULL,
    related_word_id INT NOT NULL,
    FOREIGN KEY (word_id) REFERENCES word(id) ON DELETE CASCADE,
    FOREIGN KEY (related_word_id) REFERENCES word(id) ON DELETE CASCADE
);
 */
@Entity
@Table(name = "word_relation")
@Data
public class WordRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "word_id")
    private Word word;

    @ManyToOne
    @JoinColumn(name = "related_word_id")
    private Word relatedWord;
}

