package com.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.models.EUnit;
import com.models.Unit;

public interface UnitRepository extends MongoRepository<Unit, String>{
	Optional<Unit> findByName(EUnit name);
}
