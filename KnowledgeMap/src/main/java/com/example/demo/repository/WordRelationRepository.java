package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.WordRelation;

public interface WordRelationRepository extends JpaRepository<WordRelation,Integer>{
	public void deleteByRelatedWordId(Integer id);
	public void deleteByWordId(Integer id);
}
