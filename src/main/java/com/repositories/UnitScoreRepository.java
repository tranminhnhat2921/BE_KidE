package com.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.models.UnitScore;

public interface UnitScoreRepository extends MongoRepository<UnitScore, String>{

}
