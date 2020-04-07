package guru.springframework.sfgpetclinic.services.springdatajpa;

import guru.springframework.sfgpetclinic.model.Owner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//@Disabled(value = "Disable by me")
class OwnerSDJpaServiceTest {

	OwnerSDJpaService service;

	@BeforeEach
	void setUp() {
		service = new OwnerSDJpaService(null, null, null);
	}

	@Disabled
	@Test
	void findByLastName() {
		Owner foundOwner = service.findByLastName("Buck");
	}

	@DisplayName("Name of the test")
	@Test
	void findAllByLastNameLike() {
		assertEquals("test", "test");
	}

	@Test
	void findAll() {
	}

	@Test
	void findById() {
	}

	@Test
	void save() {
	}

	@Test
	void delete() {
	}

	@Test
	void deleteById() {
	}
}