package guru.springframework.sfgpetclinic.services.springdatajpa;

import guru.springframework.sfgpetclinic.model.Speciality;
import guru.springframework.sfgpetclinic.repositories.SpecialtyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialitySDJpaServiceTest {

	@Mock
	SpecialtyRepository specialtyRepository;

	@InjectMocks
	SpecialitySDJpaService specialitySDJpaService;

	@Test
	void findByIdTest(){
		Speciality speciality = new Speciality();
		when(specialtyRepository.findById(1L)).thenReturn(Optional.of(speciality));
		Speciality foundSpeciality = specialitySDJpaService.findById(1L);
		assertThat(foundSpeciality).isNotNull();
		verify(specialtyRepository).findById(1L);
	}

	@Test
	void deleteById() {
		specialitySDJpaService.deleteById(1L);
		specialitySDJpaService.deleteById(1L);
		verify(specialtyRepository, times(2)).deleteById(1L);
	}

	@Test
	void deleteAtLeastOnce() {
		specialitySDJpaService.deleteById(1L);
		specialitySDJpaService.deleteById(1L);
		verify(specialtyRepository, atLeastOnce()).deleteById(1L);
	}

	@Test
	void deleteByIdAtMost() {
		specialitySDJpaService.deleteById(1L);
		specialitySDJpaService.deleteById(1L);
		verify(specialtyRepository, atMost(5)).deleteById(1L);
	}

	@Test
	void deleteByIdNever() {
		specialitySDJpaService.deleteById(1L);
		specialitySDJpaService.deleteById(1L);
		verify(specialtyRepository, atLeastOnce()).deleteById(1L);
		verify(specialtyRepository, never()).deleteById(5L);
	}

	@Test
	void testDelete() {
		specialitySDJpaService.delete(new Speciality());
	}

	@Test
	void testDeleteByObject() {
		Speciality speciality = new Speciality();
		specialitySDJpaService.delete(speciality);
		verify(specialtyRepository).delete(any(Speciality.class));
	}

	@Test
	void testDeleteByObjectBDDTest() {
		//given
		Speciality speciality = new Speciality();

		//when
		specialitySDJpaService.delete(speciality);

		//then
		then(specialtyRepository).should().delete(any(Speciality.class));
	}

	@Test
	void findByIdBDDTest() {
		//given
		Speciality speciality = new Speciality();
		given(specialtyRepository.findById(1L)).willReturn(Optional.of(speciality));

		//when
		Speciality foundSpeciality = specialitySDJpaService.findById(1L);

		//then
		assertThat(foundSpeciality).isNotNull();
		then(specialtyRepository).should().findById(anyLong());
		then(specialtyRepository).shouldHaveNoMoreInteractions();
	}

	@Test
	void deleteByIdBDDTest() {
		//given - none

		//when
		specialitySDJpaService.deleteById(1L);
		specialitySDJpaService.deleteById(1L);

		//then
		then(specialtyRepository).should(times(2)).deleteById(1L);
	}

	@Test
	void deleteAtLeastOnceBDDTest() {
		//given - none

		//when
		specialitySDJpaService.deleteById(1L);
		specialitySDJpaService.deleteById(1L);
		//then
		then(specialtyRepository).should(atLeastOnce()).deleteById(1L);
	}

	@Test
	void deleteByIdAtMostBDDTest() {
		//given - none

		//when
		specialitySDJpaService.deleteById(1L);
		specialitySDJpaService.deleteById(1L);
		//then
		then(specialtyRepository).should(atMost(2)).deleteById(1L);
	}

	@Test
	void deleteByIdNeverBDDTest() {
		//given - none

		//when
		specialitySDJpaService.deleteById(1L);
		specialitySDJpaService.deleteById(1L);

		//then
		then(specialtyRepository).should(atLeastOnce()).deleteById(1L);
		then(specialtyRepository).should(never()).deleteById(2L);
	}

	@Test
	void deleteBDDTest() {
		// given - none

		//when
		specialitySDJpaService.delete(new Speciality());

		//then
		then(specialtyRepository).should().delete(any());
	}
}