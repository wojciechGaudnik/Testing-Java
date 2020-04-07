package guru.springframework.sfgpetclinic.model;

import guru.springframework.sfgpetclinic.ModelRepeatedTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersonRepeatedTests implements ModelRepeatedTest {

	@RepeatedTest(value = 10, name = "{displayName} : {currentRepetition} - {totalRepetitions}")
	@DisplayName("My Repeated Test")
	void groupedAssertions(){
		Person person = new Person(1l, "Joe", "Buck");
		assertAll("Test props Set",
				() -> assertEquals("Joe", person.getFirstName(), "first"),
				() -> assertEquals("Buck", person.getLastName(), "second"));
	}

	@RepeatedTest(5)
	void myRepeatedTestWithId(TestInfo testInfo, RepetitionInfo repetitionInfo){
		System.out.println(testInfo.getDisplayName() + " : " + repetitionInfo.getCurrentRepetition());
	}

	@RepeatedTest(value = 5, name = "{displayName} : {currentRepetition} - {totalRepetitions}")
	@DisplayName("My Assigment Repeated Test")
	void myAssigmentRepeated(){
		System.out.println("test");
	}
}
