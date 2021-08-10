package nl.rabobank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.rabobank.account.Account;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.dto.PostPowerOfAttorneyDto;
import nl.rabobank.service.AccountFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Collections;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableWebMvc
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {PowerOfAttorneyController.class})
@EnableSpringDataWebSupport
class PowerOfAttorneyControllerTest {

	@MockBean
	private AccountFacade accountFacade;

	private MockMvc mockMvc;

	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext wac;

	@BeforeEach
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.objectMapper = new ObjectMapper();
	}

	@Test
	void createPowerOfAttorneyBadRequestException() throws Exception {
		// Given
		PostPowerOfAttorneyDto powerOfAttorneyDto = new PostPowerOfAttorneyDto();

		// When
		doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uh oh")).when(accountFacade).createPowerOfAttorney(powerOfAttorneyDto);
		mockMvc.perform(post("/v1/poa/create")
				.content(objectMapper.writeValueAsString(powerOfAttorneyDto))
				.contentType(MediaType.APPLICATION_JSON))
				// Then
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage(), containsString("Uh oh")));
	}

	@Test
	void createPowerOfAttorneyOk() throws Exception {
		// Given
		PostPowerOfAttorneyDto powerOfAttorneyDto = new PostPowerOfAttorneyDto();
		powerOfAttorneyDto.setAuthorization(Authorization.READ);
		powerOfAttorneyDto.setGrantorName("Walter de Rochebrune");
		powerOfAttorneyDto.setGranteeName("Koos Koets");
		powerOfAttorneyDto.setAccountNumber("1234");

		// When
		doNothing().when(accountFacade).createPowerOfAttorney(powerOfAttorneyDto);
		mockMvc.perform(post("/v1/poa/create")
				.content(objectMapper.writeValueAsString(powerOfAttorneyDto))
				.contentType(MediaType.APPLICATION_JSON))
				// Then
				.andExpect(status().is2xxSuccessful());

		ArgumentCaptor<PostPowerOfAttorneyDto> poaArgumentCaptor = ArgumentCaptor.forClass(PostPowerOfAttorneyDto.class);
		verify(accountFacade).createPowerOfAttorney(poaArgumentCaptor.capture());
		PostPowerOfAttorneyDto powerOfAttorneyDto1 = poaArgumentCaptor.getValue();
		assertEquals(powerOfAttorneyDto, powerOfAttorneyDto1);
	}

	@Test
	void listPowerOfAttorneyOk() throws Exception {
		// Given
		String granteeName = "Koos Koets";
		Account savingsAccount = SavingsAccount.builder()
				.accountHolderName(granteeName)
				.accountNumber("1234")
				.balance(1d)
				.build();

		PowerOfAttorney powerOfAttorney = PowerOfAttorney.builder()
				.account(savingsAccount)
				.authorization(Authorization.WRITE)
				.granteeName(granteeName)
				.grantorName("Walter de Rochebrune")
				.build();

		// When
		when(accountFacade.getPowerOfAttorneyForGrantee(granteeName)).thenReturn(Collections.singletonList(powerOfAttorney));
		mockMvc.perform(get("/v1/poa/list/?granteeName=" + granteeName)
				.contentType(MediaType.APPLICATION_JSON))
				// Then
				.andExpect(status().is2xxSuccessful());

		ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
		verify(accountFacade).getPowerOfAttorneyForGrantee(stringArgumentCaptor.capture());
		String granteeNamePassed = stringArgumentCaptor.getValue();
		assertEquals(granteeName, granteeNamePassed);
	}
}
