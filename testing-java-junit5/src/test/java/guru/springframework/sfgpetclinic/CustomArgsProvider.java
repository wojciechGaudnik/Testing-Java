package guru.springframework.sfgpetclinic;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class CustomArgsProvider implements ArgumentsProvider {
	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
		return Stream.of(
				Arguments.of("FL", 131, 131),
				Arguments.of("OH", 222, 222),
				Arguments.of("MI", 313, 313)
		);
	}
}
