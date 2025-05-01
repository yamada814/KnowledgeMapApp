package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.WordRelation;

public interface WordRelationRepository extends JpaRepository<WordRelation,Integer>{
	void deleteByRelatedWordId(Integer id);
	void deleteByWordId(Integer id);
}
