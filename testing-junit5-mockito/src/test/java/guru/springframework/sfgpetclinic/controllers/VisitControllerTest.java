package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.model.Pet;
import guru.springframework.sfgpetclinic.model.Visit;
import guru.springframework.sfgpetclinic.services.PetService;
import guru.springframework.sfgpetclinic.services.VisitService;
import guru.springframework.sfgpetclinic.services.map.PetMapService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VisitControllerTest {

	@Mock(lenient = true)
	VisitService visitService;

	@Mock(lenient = true)
	PetService petService;

	@Spy
	PetMapService petServiceSpy;

	@InjectMocks
	VisitController visitController;

	@Test
	void loadPetWithVisitSpy() {
		//given
		Map<String, Object> model = new HashMap<>();
		Pet pet1 = new Pet(1L);
		Pet pet2 = new Pet(2L);
		petServiceSpy.save(pet1);
		petServiceSpy.save(pet2);
		given(petServiceSpy.findById(anyLong())).willCallRealMethod();

		//when
		Visit visit = visitController.loadPetWithVisit(2L, model);

		//then
		assertThat(visit).isNotNull();
		assertThat(visit.getPet()).isNotNull();
		assertThat(visit.getPet().getId()).isEqualTo(2);
		verify(petServiceSpy, times(1)).findById(anyLong());
	}

	@Test
	void loadPetWithVisitWithStubbing() {
		//given
		Map<String, Object> model = new HashMap<>();
		Pet pet1 = new Pet(1L);
		Pet pet2 = new Pet(2L);
		petServiceSpy.save(pet1);
		petServiceSpy.save(pet2);
		given(petServiceSpy.findById(anyLong())).willReturn(pet2);

		//when
		Visit visit = visitController.loadPetWithVisit(2L, model);

		//then
		assertThat(visit).isNotNull();
		assertThat(visit.getPet()).isNotNull();
		assertThat(visit.getPet().getId()).isEqualTo(2);
//		verify(petServiceSpy, times(1)).findById(anyLong());
	}

//	@Test
//	void loadPetWithVisit() {
//		//given
//		Map<String, Object> model = new HashMap<>();
//		Pet pet = new Pet(3L);
//		given(petService.findById(anyLong())).willReturn(pet);
//
//		//when
//		Visit visit = visitController.loadPetWithVisit(3L, model);
//
//		//then
//		assertThat(visit).isNotNull();
//		assertThat(visit.getPet()).isNotNull();
//	}
}