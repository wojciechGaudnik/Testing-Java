package guru.springframework.sfgpetclinic.services.springdatajpa;

import guru.springframework.sfgpetclinic.model.Visit;
import guru.springframework.sfgpetclinic.repositories.VisitRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.api.VerificationDataInOrderImpl;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitSDJpaServiceTest {

	@Mock
	VisitRepository visitRepository;

	@InjectMocks
	VisitSDJpaService service;

	@DisplayName("Test Find All")
	@Test
	void findAll() {
		Visit visit = new Visit();
		Set<Visit> visits = new HashSet<>();
		visits.add(visit);
		when(visitRepository.findAll()).thenReturn(visits);
		Set<Visit> foundVisits = service.findAll();
		verify(visitRepository).findAll();
//		assertThat(foundVisits).hasSameElementsAs(visits);
		assertThat(foundVisits).hasSize(1);
	}

	@Test
	void findById() {
		Visit visit = new Visit();
		when(visitRepository.findById(anyLong())).thenReturn(Optional.of(visit));
		Visit foundVisit = service.findById(1L);
		verify(visitRepository).findById(anyLong());
		assertThat(foundVisit).isNotNull();
	}

	@Test
	void save() {
		Visit visit = new Visit();
		when(visitRepository.save(any(Visit.class))).thenReturn(visit);
		Visit savedVisit = service.save(new Visit());
		verify(visitRepository).save(any(Visit.class));
		assertThat(savedVisit).isNotNull();
	}

	@Test
	void delete() {
		Visit visit = new Visit();
		service.delete(visit);
		verify(visitRepository).delete(any(Visit.class));
	}

	@Test
	void deleteById() {
		service.deleteById(1L);
		verify(visitRepository).deleteById(anyLong());
	}



	@Test
	void findAllBDDTest() {
		//given
		Visit visit = new Visit();
		Set<Visit> visits = new HashSet<>();
		visits.add(visit);
		given(visitRepository.findAll()).willReturn(visits);

		//when
		Set<Visit> foundVisits = service.findAll();

		//then
		then(visitRepository).should().findAll();
		assertThat(foundVisits).hasSize(1);
	}

	@Test
	void findByIdBDDTest() {
		//given
		Visit visit = new Visit();
		given(visitRepository.findById(anyLong())).willReturn(Optional.of(visit));

		//when
		Visit foundVisit = service.findById(1L);

		//then
		then(visitRepository).should().findById(anyLong());
		assertThat(foundVisit).isNotNull();
	}

	@Test
	void saveBDDTest() {
		//given
		Visit visit = new Visit();
		given(visitRepository.save(any(Visit.class))).willReturn(visit);

		//when
		Visit savedVisit = service.save(new Visit());

		//then
		then(visitRepository).should().save(any(Visit.class));
		assertThat(savedVisit).isNotNull();
	}

	@Test
	void deleteBDDTest() {
		//given
		Visit visit = new Visit();

		//when
		service.delete(visit);

		//then
		then(visitRepository).should().delete(any(Visit.class));
	}

	@Test
	void deleteByIdBDDTest() {
		//given - none

		//when
		service.deleteById(1L);

		//then
		then(visitRepository).should().deleteById(anyLong());
	}
}