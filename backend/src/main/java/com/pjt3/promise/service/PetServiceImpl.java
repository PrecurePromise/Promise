package com.pjt3.promise.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pjt3.promise.entity.Pet;
import com.pjt3.promise.entity.User;
import com.pjt3.promise.repository.PetRepository;
import com.pjt3.promise.repository.UserRepository;
import com.pjt3.promise.request.UserInsertPostReq;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService{

	private final UserRepository userRepository;
	private final PetRepository petRepository;

	@Override
	public int increasePetExp(int petExp, User user) {
		try {
			Pet pet = petRepository.findPetByUser(user);

			// 경험치 증가
			int incExp = pet.getPetExp() + petExp;
			pet.updatePetExp(incExp);

			// 경험치 점수에 따른 레벨 변경
			if(100 <= incExp && incExp < 500) {
				pet.updatePetLevel(2);
			} else if(500 <= incExp && incExp < 1000) {
				pet.updatePetLevel(3);
			} else if(1000 <= incExp && incExp < 10000){
				pet.updatePetLevel(4);
			} else if(incExp >= 10000) {
				pet.updatePetLevel(5);
			}

			petRepository.save(pet);

			return 1;
		} catch(Exception e) {
			return -1;
		}
	}

	@Override
	public Pet insertPet(UserInsertPostReq userInsertInfo) {
		User user = userRepository.findUserByUserEmail(userInsertInfo.getUserEmail());
		Pet pet = Pet.builder()
				.user(user)
				.petName(userInsertInfo.getPetName())
				.petLevel(1)
				.build();

		return petRepository.save(pet);

	}

}