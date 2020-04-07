package guru.springframework.sfgpetclinic.model;

import guru.springframework.sfgpetclinic.CustomArgsProvider;
import guru.springframework.sfgpetclinic.ModelTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.StringWriter;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

//@Tag("model")
class OwnerTest implements ModelTests {

	@Test
	void dependentAssertions(){
		Owner owner = new Owner(1l, "Joe", "Buck");
		owner.setCity("Key West");
		owner.setTelephone("1231231234");

		assertAll("Properties Test",
				() -> assertAll("Person Properties",
						() -> assertEquals("Joe", owner.getFirstName()),
						() -> assertEquals("Buck", owner.getLastName())),
				() -> assertAll("Owner Properties",
						() -> assertEquals("Key West", owner.getCity()),
						() -> assertEquals("1231231234", owner.getTelephone())));

		assertThat(owner.getCity(), is("Key West"));
	}

	@DisplayName("Value Source Test")
	@ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
	@ValueSource(strings = {"Spring", "Framework", "Guru"})
	void testValueSource(String val) {
		assertEquals(1,1);
		System.out.println(val);
	}

	@DisplayName("Enum Source Test")
	@ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
	@EnumSource(OwnerType.class)
	void enumTest(OwnerType ownerType) {
		assertEquals(1,1);
		System.out.println(ownerType);
	}

	@DisplayName("CSV Source Test")
	@ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
	@CsvSource({
			"FL, 1, 1",
			"OH, 2, 2",
			"MI, 3, 3"
	})
	void csvInputTest(String stateName, int val1, int val2) {
		assertEquals(1,1);
		System.out.println(stateName + val1 + val2);
	}

	@DisplayName("CSV File Source Test")
	@ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
	@CsvFileSource(resources = "/input.csv", numLinesToSkip = 1)
	void csvFileInputTest(String stateName, int val1, int val2) {
		assertEquals(1,1);
		System.out.println(stateName + val1 + val2);
	}

	@DisplayName("Method provider Test")
	@ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
	@MethodSource("getArgs")
	void fromMethodTest(String stateName, int val1, int val2) {
		assertEquals(1,1);
		System.out.println(stateName + val1 + val2);
	}

	@DisplayName("Class provider Test")
	@ParameterizedTest(name = "{displayName} - [{index}] {arguments}")
	@ArgumentsSource(CustomArgsProvider.class)
	void fromClassTest(String stateName, int val1, int val2) {
		assertEquals(1,1);
		System.out.println(stateName + val1 + val2);
	}



	static Stream<Arguments> getArgs(){
		return Stream.of(
				Arguments.of("FL", 13, 13),
				Arguments.of("OH", 22, 22),
				Arguments.of("MI", 31, 31)
		);
	}

}