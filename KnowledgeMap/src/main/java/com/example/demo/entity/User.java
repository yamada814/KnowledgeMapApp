package com.example.demo.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.ToString;

/*
 * ログインユーザ
 * CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL 
);
INSERT INTO users(username,password) 
values ('a','$2a$10$TDKTbY8waJVqKoN0u6XLDOi4TwW4ws/v6SX3bLhT8NLMD/y/fjnQK');-- test1234
INSERT INTO users(username,password) 
values ('b','$2a$10$UIJ2i1EGfCh4zFx.rfCthuVEq0.K5Y5hBLCi2nBYRYS3dTHR5dtWO');-- test0000

 */
@Entity
@Data
@Table(name="users")
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="username")
	private String username;
	
	@Column(name="password")
	private String password;
	
	@OneToMany(mappedBy="user",cascade=CascadeType.ALL,orphanRemoval=true)
	@ToString.Exclude//ログ出力時に循環参照によるStackOverflowErrorを防ぐ
	private List<Wordbook> wordbooks;

}
