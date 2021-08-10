package nl.rabobank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.rabobank.account.AccountType;
import nl.rabobank.dto.AccountDto;
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

import java.util.Objects;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@EnableWebMvc
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {AccountController.class})
@EnableSpringDataWebSupport
class AccountControllerTest {

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
	void createAccountBadRequestException() throws Exception {
		// Given
		AccountDto accountDto = new AccountDto();

		// When
		doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uh oh")).when(accountFacade).createAccount(accountDto);
		mockMvc.perform(post("/v1/account/create")
				.content(objectMapper.writeValueAsString(accountDto))
				.contentType(MediaType.APPLICATION_JSON))
				// Then
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage(), containsString("Uh oh")));
	}

	@Test
	void createAccountOk() throws Exception {
		// Given
		AccountDto accountDto = new AccountDto();
		accountDto.setAccountNumber("1234");
		accountDto.setAccountType(AccountType.PAYMENT);
		accountDto.setAccountHolderName("Walter de Rochebrune");
		accountDto.setBalance(1_000_000d);

		// When
		doNothing().when(accountFacade).createAccount(accountDto);
		mockMvc.perform(post("/v1/account/create")
				.content(objectMapper.writeValueAsString(accountDto))
				.contentType(MediaType.APPLICATION_JSON))
				// Then
				.andExpect(status().is2xxSuccessful());

		ArgumentCaptor<AccountDto> accountArgumentCaptor = ArgumentCaptor.forClass(AccountDto.class);
		verify(accountFacade).createAccount(accountArgumentCaptor.capture());
		AccountDto accountDto1 = accountArgumentCaptor.getValue();
		assertEquals(accountDto, accountDto1);
	}
}
