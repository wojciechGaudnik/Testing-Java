package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.ControllerTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class IndexControllerTest implements ControllerTests {

	IndexController controller;

	@BeforeEach
	void setUp() {
		controller = new IndexController();
	}

	@Test
	void index() {
		assertEquals("index", controller.index());
		assertEquals("index", controller.index(), "Wrong");

		assertThat(controller.index()).isEqualTo("index");
	}

	@Test
	void oupsHandler() throws ValueNotFoundException {
//		assertEquals("index", controller.index());
//		assertEquals("notimplemented", controller.oupsHandler(), () -> "This is some expensive");
		assertThrows(ValueNotFoundException.class, () -> controller.oupsHandler());

	}

	@Disabled
	@Test
	void testTimeOut(){
		assertTimeout(Duration.ofMillis(100), () -> {
			Thread.sleep(5000);
			System.out.println("I got here");
		});
	}

	@Disabled
	@Test
	void testTimeOutPreempt(){
		assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
			Thread.sleep(5000);
			System.out.println("I not got here");
		});
	}

	@Test
	void testAssumptionTrue() {
		assumeTrue("BQ666".equalsIgnoreCase(System.getenv("BQ666_RUNTIME")));
	}

	@Test
	void testAssumptionTrueAssumptionIsTrue() {
		assumeTrue("BQ666".equalsIgnoreCase("BQ666"));
	}

	@EnabledOnOs(value = OS.LINUX)
	@Test
	void osTest() {
		System.out.println("test");
		assertTrue(true);
	}


}