package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;
/*
CREATE TABLE category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    wordbook_id INT,
    FOREIGN KEY (wordbook_id) REFERENCES wordbook(id),
    UNIQUE(wordbook_id,name)
);
INSERT INTO category(name,wordbook_id) values("cate1",1);
INSERT INTO category(name,wordbook_id) values("cate2",1);
 */
@Entity
@Table(name="category")
@Data
public class Category {
	
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;
    
    @ManyToOne
    @JoinColumn(name="wordbook_id")
    private Wordbook wordbook;
}

