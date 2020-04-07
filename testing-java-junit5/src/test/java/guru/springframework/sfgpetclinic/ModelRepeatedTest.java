package guru.springframework.sfgpetclinic;

import org.junit.jupiter.api.*;

@Tag("Repeated")
public interface ModelRepeatedTest {
//	@RepeatedTest(2)
	@BeforeEach
	default void beforeEachConsoleOutputted(TestInfo testInfo, RepetitionInfo repetitionInfo){
		System.out.println("Running test - " + testInfo.getDisplayName() + " - " + repetitionInfo.getTotalRepetitions());
	}
}
