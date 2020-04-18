package guru.springframework.sfgpetclinic.services.springdatajpa;

import guru.springframework.sfgpetclinic.model.Speciality;
import guru.springframework.sfgpetclinic.repositories.SpecialtyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.ls.LSProgressEvent;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialitySDJpaServiceTest {

	@Mock(lenient = true)
	SpecialtyRepository specialityRepository;

	@InjectMocks
	SpecialitySDJpaService specialitySDJpaService;

	@Test
	void findByIdTest(){
		Speciality speciality = new Speciality();
		when(specialityRepository.findById(1L)).thenReturn(Optional.of(speciality));
		Speciality foundSpeciality = specialitySDJpaService.findById(1L);
		assertThat(foundSpeciality).isNotNull();
		verify(specialityRepository).findById(1L);
	}

	@Test
	void deleteById() {
		specialitySDJpaService.deleteById(1L);
		specialitySDJpaService.deleteById(1L);
		verify(specialityRepository, times(2)).deleteById(1L);
	}

	@Test
	void deleteAtLeastOnce() {
		specialitySDJpaService.deleteById(1L);
		specialitySDJpaService.deleteById(1L);
		verify(specialityRepository, atLeastOnce()).deleteById(1L);
	}

	@Test
	void deleteByIdAtMost() {
		specialitySDJpaService.deleteById(1L);
		specialitySDJpaService.deleteById(1L);
		verify(specialityRepository, atMost(5)).deleteById(1L);
	}

	@Test
	void deleteByIdNever() {
		specialitySDJpaService.deleteById(1L);
		specialitySDJpaService.deleteById(1L);
		verify(specialityRepository, atLeastOnce()).deleteById(1L);
		verify(specialityRepository, never()).deleteById(5L);
	}

	@Test
	void testDelete() {
		specialitySDJpaService.delete(new Speciality());
	}

	@Test
	void testDeleteByObject() {
		Speciality speciality = new Speciality();
		specialitySDJpaService.delete(speciality);
		verify(specialityRepository).delete(any(Speciality.class));
	}

	@Test
	void testDeleteByObjectBDDTest() {
		//given
		Speciality speciality = new Speciality();

		//when
		specialitySDJpaService.delete(speciality);

		//then
		then(specialityRepository).should().delete(any(Speciality.class));
	}

	@Test
	void findByIdBDDTest() {
		//given
		Speciality speciality = new Speciality();
		given(specialityRepository.findById(1L)).willReturn(Optional.of(speciality));

		//when
		Speciality foundSpeciality = specialitySDJpaService.findById(1L);

		//then
		assertThat(foundSpeciality).isNotNull();
		then(specialityRepository).should().findById(anyLong());
		then(specialityRepository).shouldHaveNoMoreInteractions();
	}

	@Test
	void deleteByIdBDDTest() {
		//given - none

		//when
		specialitySDJpaService.deleteById(1L);
		specialitySDJpaService.deleteById(1L);

		//then
		then(specialityRepository).should(times(2)).deleteById(1L);
	}

	@Test
	void deleteAtLeastOnceBDDTest() {
		//given - none

		//when
		specialitySDJpaService.deleteById(1L);
		specialitySDJpaService.deleteById(1L);
		//then
		then(specialityRepository).should(atLeastOnce()).deleteById(1L);
	}

	@Test
	void deleteByIdAtMostBDDTest() {
		//given - none

		//when
		specialitySDJpaService.deleteById(1L);
		specialitySDJpaService.deleteById(1L);
		//then
		then(specialityRepository).should(atMost(2)).deleteById(1L);
	}

	@Test
	void deleteByIdNeverBDDTest() {
		//given - none

		//when
		specialitySDJpaService.deleteById(1L);
		specialitySDJpaService.deleteById(1L);

		//then
		then(specialityRepository).should(atLeastOnce()).deleteById(1L);
		then(specialityRepository).should(never()).deleteById(2L);
	}

	@Test
	void deleteBDDTest() {
		// given - none

		//when
		specialitySDJpaService.delete(new Speciality());

		//then
		then(specialityRepository).should().delete(any());
	}

	@Test
	void testDoThrow() {
		doThrow(new RuntimeException("boom")).when(specialityRepository).delete(any());
		assertThrows(RuntimeException.class, () -> specialityRepository.delete(new Speciality()));
		verify(specialityRepository).delete(any());
	}

	@Test
	void testFindByIDThrows() {
		given(specialityRepository.findById(1L)).willThrow(new RuntimeException("boom"));

		assertThrows(RuntimeException.class, () -> specialitySDJpaService.findById(1L));

		then(specialityRepository).should().findById(1L);
	}

	@Test
	void testDeleteBDD() {
		willThrow(new RuntimeException("boom")).given(specialityRepository).delete(any());

		assertThrows(RuntimeException.class, () -> specialityRepository.delete(new Speciality()));

		then(specialityRepository).should().delete(any());
	}

	@Test
	void testSaveLambda() {
		//given
		final String MATCH_ME = "MATCH_ME";
		var speciality = new Speciality();
		speciality.setDescription(MATCH_ME);

		Speciality savedSpeciality = new Speciality();
		savedSpeciality.setId(1L);

		//need mock to only return on match MATCH_ME string
		given(specialityRepository.save(argThat(argument -> argument.getDescription().equals(MATCH_ME)))).willReturn(savedSpeciality);

		//when
		Speciality returnedSpeciality = specialitySDJpaService.save(speciality);

		//then
		assertThat(returnedSpeciality.getId()).isEqualTo(1L);
	}

	@Test
	void testSaveLambdaNoMatch() {
		//given
		final String MATCH_ME = "MATCH_ME";
		var speciality = new Speciality();
		speciality.setDescription("not a match");

		Speciality savedSpeciality = new Speciality();
		savedSpeciality.setId(1L);

		//need mock to only return on match MATCH_ME string
		given(specialityRepository.save(argThat(argument -> argument.getDescription().equals(MATCH_ME)))).willReturn(savedSpeciality);

		//when
		Speciality returnedSpeciality = specialitySDJpaService.save(speciality);

		//then
		assertNull(returnedSpeciality);
	}
}