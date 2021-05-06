package com.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.models.ERole;
import com.models.Role;

public interface RoleRepository extends MongoRepository<Role, String>{
	  Optional<Role> findByName(ERole name);
}
