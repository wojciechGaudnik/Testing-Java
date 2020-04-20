package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.fauxspring.Model;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

	private static final String OWNERS_CREATE_OR_UPDATE_OWNER_FORM = "owners/createOrUpdateOwnerForm";
	private static final String REDIRECT_OWNERS_5 = "redirect:/owners/5";

	@Mock(lenient = true)
	OwnerService service;

	@InjectMocks
	OwnerController controller;

	@Mock
	Model model;

	@Mock
	BindingResult bindingResult;

	@Captor
	ArgumentCaptor<String> stringArgumentCaptor;

	@BeforeEach
	void setUp(){
		given(service.findAllByLastNameLike(stringArgumentCaptor.capture())).willAnswer(invocationOnMock -> {
			List<Owner> owners = new ArrayList<>();
			String name = invocationOnMock.getArgument(0);
			if (name.equals("%Buck%")) {
				owners.add(new Owner(1L, "Joe", "Buck"));
				return owners;
			} else if (name.equals("%DontFindMe%")) {
				return owners;
			} else if (name.equals("%FindMe%")) {
				owners.add(new Owner(1L, "Joe", "Buck"));
				owners.add(new Owner(2L, "Joe2", "Buck2"));
				return owners;
			}
			throw new RuntimeException("Invalid Argument");
		});
	}

	@Test
	void processFindFormWildCardString() {
		//given
		Owner owner = new Owner(1L, "Joe", "Buck");

		//when
		String viewName = controller.processFindForm(owner, bindingResult, null);

		//then
			assertThat("%Buck%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
			assertThat("redirect:/owners/1").isEqualToIgnoringCase(viewName);

	}

	@Test
	void processFindFormWildCardNotFound() {
		//given
		Owner owner = new Owner(1L, "Joe", "DontFindMe");

		//when
		String viewName = controller.processFindForm(owner, bindingResult, null);
//		verifyNoInteractions(model);


		//then
		assertThat("%DontFindMe%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
		assertThat("owners/findOwners").isEqualToIgnoringCase(viewName);
//		verifyNoMoreInteractions(service);
//		verifyNoInteractions(model);
	}

	@Test
	void processFindFormWildCardFound() {
		//given
		Owner owner = new Owner(1L, "Joe", "FindMe");
		InOrder inOrder = inOrder(model, service);

		//when
		String viewName = controller.processFindForm(owner, bindingResult, model);

		//then
		assertThat("%FindMe%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
		assertThat("owners/ownersList").isEqualToIgnoringCase(viewName);

		//inOrder asserts
		inOrder.verify(service).findAllByLastNameLike(anyString());
		inOrder.verify(model, times(1)).addAttribute(anyString(), anyList());
		verifyNoMoreInteractions(model);
	}

	@Test
	void processCreationFormHasErrors() {
		//given
		Owner owner = new Owner(1L, "Jim", "Bob");
		given(bindingResult.hasErrors()).willReturn(true);

		//when
		String viewName = controller.processCreationForm(owner, bindingResult);

		//then
		assertThat(viewName).isEqualToIgnoringCase(OWNERS_CREATE_OR_UPDATE_OWNER_FORM);
		verifyNoInteractions(model);
	}

	@Test
	void processCreationFormHasNoErrors() {
		//given
		Owner owner = new Owner(5L, "Jim", "Bob");
		given(bindingResult.hasErrors()).willReturn(false);
		given(service.save(any())).willReturn(owner);

		//when
		String viewName = controller.processCreationForm(owner, bindingResult);

		//then
		assertThat(viewName).isEqualToIgnoringCase(REDIRECT_OWNERS_5);
		verifyNoInteractions(model);
	}

//	@Test
//	void processFindFormWildCardString() {
//		//given
//		Owner owner = new Owner(1L, "Joe", "Buck");
//		List<Owner> ownerList = new ArrayList<>();
//todo		final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
//		given(service.findAllByLastNameLike(captor.capture())).willReturn(ownerList);
//
//		//when
//		String viewName = controller.processFindForm(owner, bindingResult, null);
//
//		//then
//		assertThat("%Buck%").isEqualToIgnoringCase(captor.getValue());
//	}
//
//	@Test
//	void processFindFormWildCardStringAnnotation() {
//		//given
//		Owner owner = new Owner(1L, "Joe", "Buck");
//		List<Owner> ownerList = new ArrayList<>();
//		given(service.findAllByLastNameLike(stringArgumentCaptor.capture())).willReturn(ownerList);
//
//		//when
//		String viewName = controller.processFindForm(owner, bindingResult, null);
//
//		//then
//		assertThat("%Buck%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
//	}
}