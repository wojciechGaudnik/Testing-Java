import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GreetingTest {

	private Greeting greeting;

	@BeforeAll
	public static void beforeClass(){
		System.out.println("Before - I am only called Once!!!");
	}

	@BeforeEach
	void setUp() {
		greeting = new Greeting();
	}

	@Test
	void helloWorld() {
		assertEquals("test", "test");
		System.out.println(greeting.helloWorld());
	}

	@Test
	void helloWorld1() {
		System.out.println(greeting.helloWorld("Wojtek"));

	}

	@AfterEach
	void tearDown(){
		System.out.println("In After Each ...");
	}

	@AfterAll
	public static void afterClass(){
		System.out.println("After - I am only called Once!!!");
	}
}