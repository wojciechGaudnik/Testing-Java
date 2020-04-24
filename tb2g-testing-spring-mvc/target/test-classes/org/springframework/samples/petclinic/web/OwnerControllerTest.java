package org.springframework.samples.petclinic.web;

import org.aspectj.lang.annotation.After;
import org.assertj.core.util.Lists;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//todo this is proper way to test my mvc !!!
@ExtendWith(MockitoExtension.class)
@SpringJUnitWebConfig(locations = {"classpath:spring/mvc-test-config.xml", "classpath:spring/mvc-core-config.xml"})
class OwnerControllerTest {

	@Autowired
	OwnerController ownerController;

	@Autowired
	ClinicService clinicService;
	
	MockMvc mockMvc;

	ArgumentCaptor<String> stringArgumentCaptor;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(ownerController).build();
	}

	@AfterEach
	void validate(){
//		validateMockitoUsage();
		reset(clinicService);
	}



	@Test
	void newOwnerUpdateValid() throws Exception {
		mockMvc
				.perform(post("/owners/{ownerId}/edit", 1)
						.param("firstName", "Jimmy")
						.param("lastName", "Buffet")
						.param("address", " 123 Duval St.")
						.param("city", "Key West")
						.param("telephone", "12341234"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void newOwnerUpdateNotValid() throws Exception {
		mockMvc
				.perform(post("/owners/{ownerId}/edit", 1)
						.param("firstName", "Jimmy")
						.param("lastName", "Buffet")
						.param("address", " 123 Duval St."))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}


	@Test
	void newOwnerPostValid() throws Exception {
		mockMvc
				.perform(post("/owners/new")
						.param("firstName", "Jimmy")
						.param("lastName", "Buffet")
						.param("address", " 123 Duval St.")
						.param("city", "Key West")
						.param("telephone", "12341234"))
				.andExpect(status().is3xxRedirection());
	}

	@Test
	void newOwnerPostNotValid() throws Exception {
		mockMvc
				.perform(post("/owners/new")
						.param("firstName", "Jimmy")
						.param("lastName", "Buffet")
						.param("City", "Key West"))
				.andExpect(status().isOk())
				.andExpect(model().attributeHasErrors("owner"))
				.andExpect(model().attributeHasFieldErrors("owner", "address"))
				.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
		.andExpect(view().name("owners/createOrUpdateOwnerForm"));

	}

	@Test
	void findOwnerOneResult() throws Exception {
		var owner = new Owner();
		owner.setId(1);
		final String findJustOne = "FindJustOne";
		owner.setLastName(findJustOne);
		given(clinicService.findOwnerByLastName(findJustOne)).willReturn(Lists.newArrayList(owner));

		mockMvc
				.perform(get("/owners").param("lastName", findJustOne))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/1"));

		then(clinicService).should().findOwnerByLastName(anyString());

//		assertThat(stringArgumentCaptor.getValue()).isEqualToIgnoringCase("");
	}

	@Test
	void returnListOfOwners() throws Exception {
		final String findJustOne = "FindJustOne";
		var owner1 = new Owner();
		owner1.setId(1);
		owner1.setLastName("a");
		var owner2 = new Owner();
		owner2.setId(2);
		owner2.setLastName("b");
		given(clinicService.findOwnerByLastName("")).willReturn(Lists.newArrayList(owner1, owner2));

		mockMvc
				.perform(get("/owners"))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/ownersList"));

		then(clinicService).should().findOwnerByLastName(stringArgumentCaptor.capture());

		System.out.println(stringArgumentCaptor.getValue() + " <--------------------");

		assertThat(stringArgumentCaptor.getValue()).isEqualToIgnoringCase("");
	}

	@Test
	void initCreationFormTest() throws Exception {
		mockMvc
				.perform(get("/owners/new"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("owner"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));

	}

	@Test
	void findByNameNotFound() throws Exception {
		mockMvc
				.perform(get("/owners").param("lastName", "Don't find Me!"))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/findOwners"));

	}

	@Test
	void tempTest() {

	}

}