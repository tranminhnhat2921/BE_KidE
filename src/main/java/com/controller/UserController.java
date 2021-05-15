package com.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.models.EAvatar;
import com.models.EUnit;
import com.models.UnitScore;
import com.models.User;
import com.payload.request.UpdateScoreRequest;
import com.payload.response.ChangeAvatarResponse;
import com.payload.response.MessageResponse;
import com.payload.response.UpdateScoreResponse;
import com.repositories.AvatarRepository;
import com.repositories.UnitScoreRepository;
import com.repositories.UserRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	UnitScoreRepository unitScoreRepository;

	@Autowired
	AvatarRepository avatarRepository;

	@PutMapping("/user/score/{id}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<?> updateScore(@PathVariable("id") String id,
			@RequestBody UpdateScoreRequest updateScoreRequest) {
		Optional<User> userData = userRepository.findById(id);
		if (userData.isPresent()) {
			User _user = userData.get();
			EUnit updateUnitName;
			int newExp = _user.getExp() + 2;
			int currentScore = 0;
			int increaseScore = 0;

			switch (updateScoreRequest.getName()) {
			case "aphabet":
				updateUnitName = EUnit.UNIT_APHABET;
				break;
			case "number":
				updateUnitName = EUnit.UNIT_NUMBER;
				break;
			case "color":
				updateUnitName = EUnit.UNIT_COLOR;
				break;
			case "animal":
				updateUnitName = EUnit.UNIT_ANIMAL;
				break;
			default:
				return ResponseEntity.accepted().body(new MessageResponse("Unit does not exist!"));
			}

			for (UnitScore unitScore : _user.getListScore()) {
				if (unitScore.getUnit().getName().equals(updateUnitName)) {
					currentScore = unitScore.getScore();
					increaseScore = updateScoreRequest.getScore() - currentScore;
					if (unitScore.getScore() < updateScoreRequest.getScore()) {					
						UnitScore _unitScore = unitScoreRepository.findById(unitScore.getId()).get();
						_unitScore.setScore(updateScoreRequest.getScore());
						unitScoreRepository.save(_unitScore);
						_user.setExp(newExp);
						userRepository.save(_user);
						return ResponseEntity.ok().body(new UpdateScoreResponse("Update score success!",
								updateScoreRequest.getName(), _unitScore.getScore(), newExp + increaseScore/10));
					}
					
					break;
				}
			}
			_user.setExp(newExp);
			userRepository.save(_user);
			return ResponseEntity.ok().body(new UpdateScoreResponse("Score less than current score!",
					updateScoreRequest.getName(), currentScore, newExp + increaseScore/10));
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/user/avatar/{id}/{avatar}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<?> updateAvatar(@PathVariable("id") String id, @PathVariable("avatar") String avatar) {
		Optional<User> userData = userRepository.findById(id);
		if (userData.isPresent()) {
			User _user = userData.get();
			switch (avatar) {
			case "cat":
				_user.setAvatar(avatarRepository.findByName(EAvatar.AVATAR_CAT).get());
				break;
			case "dinosaur":
				if (_user.getExp() >= 300) {
					_user.setAvatar(avatarRepository.findByName(EAvatar.AVATAR_DINOSAUR).get());
				} else
					return ResponseEntity.ok().body(new ChangeAvatarResponse("Avatar dinosaur chưa được mở khóa!", false));
				break;
			case "dolphin":
				if (_user.getExp() >= 500) {
					_user.setAvatar(avatarRepository.findByName(EAvatar.AVATAR_DOLPHIN).get());
				} else
					return ResponseEntity.ok().body(new ChangeAvatarResponse("Avatar dolphin chưa được mở khóa!", false));
				break;
			default:
				return ResponseEntity.notFound().build();
			}
			userRepository.save(_user);
			return ResponseEntity.ok().body(new ChangeAvatarResponse("Thay đổi avatar thành công!", true));
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
}
