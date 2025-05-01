package com.example.demo.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name="wordbook")
public class Wordbook {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="name")
	private String name;
	
	@ManyToOne
	@JoinColumn(name="user_id",nullable=false)
	//@ToString.Exclude
	private User user;
	
	@OneToMany(mappedBy="wordbook",cascade=CascadeType.ALL,orphanRemoval=true)
	private List<Category> categories;

}
