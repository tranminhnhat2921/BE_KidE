package com.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.models.Avatar;
import com.models.EAvatar;

public interface AvatarRepository extends MongoRepository<Avatar, String>{
	Optional<Avatar> findByName(EAvatar name);
}
