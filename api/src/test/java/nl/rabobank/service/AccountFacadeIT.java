package nl.rabobank.service;


import nl.rabobank.account.Account;
import nl.rabobank.account.AccountType;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.dto.AccountDto;
import nl.rabobank.dto.PostPowerOfAttorneyDto;
import nl.rabobank.repository.AccountRepository;
import nl.rabobank.repository.PowerOfAttorneyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest(includeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
		pattern = "(nl.rabobank.service.*|nl.rabobank.mapper.*)"))
@ExtendWith(SpringExtension.class)
class AccountFacadeIT {

	@Autowired
	AccountFacade accountFacade;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	PowerOfAttorneyRepository powerOfAttorneyRepository;

	@AfterEach
	void TearDown() {
		mongoTemplate.getDb().drop();
	}

	@Test
	void createAccountAccountNumberExists() {
		// Given
		AccountDto accountDto1 = createAccount("1234", "Walter de Rochebrune", 1d, AccountType.SAVINGS);
		accountFacade.createAccount(accountDto1);
		AccountDto accountDto2 = createAccount("1234", "Koos Koets", 1d, AccountType.PAYMENT);

		// When
		Exception exception = assertThrows(ResponseStatusException.class, () -> accountFacade.createAccount(accountDto2));

		// Then
		assertTrue(exception.getMessage().contains("Account number already exists"));
	}

	@Test
	void createAccountPaymentOk() {
		// Given
		AccountDto accountDto = createAccount("1234", "Walter de Rochebrune", 1d, AccountType.PAYMENT);

		// When
		accountFacade.createAccount(accountDto);

		// Then
		List<Account> accounts = accountRepository.findAll();
		assertEquals(1, accounts.size());
		assertEquals(accountDto.getAccountNumber(), accounts.get(0).getAccountNumber());
		assertEquals(accountDto.getAccountHolderName(), accounts.get(0).getAccountHolderName());
		assertEquals(accountDto.getBalance(), accounts.get(0).getBalance());
	}

	@Test
	void createAccountSavingsOk() {
		// Given
		AccountDto accountDto = createAccount("1234", "Walter de Rochebrune", 1d, AccountType.SAVINGS);

		// When
		accountFacade.createAccount(accountDto);

		// Then
		List<Account> accounts = accountRepository.findAll();
		assertEquals(1, accounts.size());
		assertEquals(accountDto.getAccountNumber(), accounts.get(0).getAccountNumber());
		assertEquals(accountDto.getAccountHolderName(), accounts.get(0).getAccountHolderName());
		assertEquals(accountDto.getBalance(), accounts.get(0).getBalance());
	}

	@Test
	void createPowerOfAttorneyAccountDoesNotExists() {
		// Given
		PostPowerOfAttorneyDto postPowerOfAttorneyDto = createPowerOfAttorney(
				"1234",
				"Walter de Rochebrune",
				"Cor van der Laak",
				Authorization.READ);

		// When
		Exception exception = assertThrows(ResponseStatusException.class, () -> accountFacade.createPowerOfAttorney(postPowerOfAttorneyDto));

		// Then
		assertTrue(exception.getMessage().contains("Account does not exist"));
	}

	@Test
	void createPowerOfAttorneyAlreadyExists() {
		// Given
		AccountDto accountDto = createAccount("1234", "Walter de Rochebrune", 1d, AccountType.PAYMENT);
		PostPowerOfAttorneyDto postPowerOfAttorneyDto = createPowerOfAttorney(
				accountDto.getAccountNumber(),
				accountDto.getAccountHolderName(),
				"Cor van der Laak",
				Authorization.READ);
		accountFacade.createAccount(accountDto);
		accountFacade.createPowerOfAttorney(postPowerOfAttorneyDto);

		// When
		Exception exception = assertThrows(ResponseStatusException.class, () -> accountFacade.createPowerOfAttorney(postPowerOfAttorneyDto));

		// Then
		assertTrue(exception.getMessage().contains("Power Of Attorney for this account, grantee and authorization already exists"));
	}

	@Test
	void createPowerOfAttorneyGrantorIsNotAccountHolder() {
		// Given
		AccountDto accountDto = createAccount("1234", "Walter de Rochebrune", 1d, AccountType.SAVINGS);
		PostPowerOfAttorneyDto postPowerOfAttorneyDto = createPowerOfAttorney(
				accountDto.getAccountNumber(),
				"Koos Koets",
				"Cor van der Laak",
				Authorization.READ);
		accountFacade.createAccount(accountDto);

		// When
		Exception exception = assertThrows(ResponseStatusException.class, () -> accountFacade.createPowerOfAttorney(postPowerOfAttorneyDto));

		// Then
		assertTrue(exception.getMessage().contains("grantorName is not the account holder"));
	}

	@Test
	void createPowerOfAttorneyOk() {
		// Given
		AccountDto accountDto = createAccount("1234", "Walter de Rochebrune", 1d, AccountType.PAYMENT);
		PostPowerOfAttorneyDto postPowerOfAttorneyDto = createPowerOfAttorney(
				accountDto.getAccountNumber(),
				accountDto.getAccountHolderName(),
				"Cor van der Laak",
				Authorization.READ);
		accountFacade.createAccount(accountDto);

		// When
		accountFacade.createPowerOfAttorney(postPowerOfAttorneyDto);

		// Then
		List<PowerOfAttorney> powerOfAttorneys = powerOfAttorneyRepository.findAll();
		assertEquals(1, powerOfAttorneys.size());
		assertEquals(postPowerOfAttorneyDto.getAccountNumber(), powerOfAttorneys.get(0).getAccount().getAccountNumber());
		assertEquals(postPowerOfAttorneyDto.getGranteeName(), powerOfAttorneys.get(0).getGranteeName());
		assertEquals(postPowerOfAttorneyDto.getGrantorName(), powerOfAttorneys.get(0).getGrantorName());
		assertEquals(postPowerOfAttorneyDto.getAuthorization(), powerOfAttorneys.get(0).getAuthorization());
	}

	@Test
	void createPowerOfAttorneyReadAndWrite() {
		// Given
		AccountDto accountDto = createAccount("1234", "Walter de Rochebrune", 1d, AccountType.PAYMENT);
		PostPowerOfAttorneyDto powerOfAttorneyDto1 = createPowerOfAttorney(
				accountDto.getAccountNumber(),
				accountDto.getAccountHolderName(),
				"Cor van der Laak",
				Authorization.READ);
		PostPowerOfAttorneyDto powerOfAttorneyDto2 = createPowerOfAttorney(
				powerOfAttorneyDto1.getAccountNumber(),
				powerOfAttorneyDto1.getGrantorName(),
				powerOfAttorneyDto1.getGranteeName(),
				Authorization.WRITE);

		accountFacade.createAccount(accountDto);
		accountFacade.createPowerOfAttorney(powerOfAttorneyDto1);

		// When
		accountFacade.createPowerOfAttorney(powerOfAttorneyDto2);

		// Then
		List<PowerOfAttorney> powerOfAttorneys = powerOfAttorneyRepository.findAll();
		assertEquals(2, powerOfAttorneys.size());
	}

	@Test
	void getPowerOfAttorneyForGrantee() {
		// Given
		String grantorName = "Walter de Rochebrune";
		String granteeName = "Cor van der Laak";

		AccountDto accountDto = createAccount("1234", grantorName, 1d, AccountType.PAYMENT);
		PostPowerOfAttorneyDto powerOfAttorneyDto1 = createPowerOfAttorney(
				accountDto.getAccountNumber(),
				accountDto.getAccountHolderName(),
				granteeName,
				Authorization.READ);
		PostPowerOfAttorneyDto powerOfAttorneyDto2 = createPowerOfAttorney(
				powerOfAttorneyDto1.getAccountNumber(),
				powerOfAttorneyDto1.getGrantorName(),
				powerOfAttorneyDto1.getGranteeName(),
				Authorization.WRITE);

		accountFacade.createAccount(accountDto);
		accountFacade.createPowerOfAttorney(powerOfAttorneyDto1);
		accountFacade.createPowerOfAttorney(powerOfAttorneyDto2);

		// When
		List<PowerOfAttorney> powerOfAttorneys = accountFacade.getPowerOfAttorneyForGrantee(granteeName);

		// Then
		assertEquals(2, powerOfAttorneys.size());
	}

	private AccountDto createAccount(String accountNumber, String accountHolderName, Double balance, AccountType accountType) {
		AccountDto accountDto = new AccountDto();
		accountDto.setAccountNumber(accountNumber);
		accountDto.setAccountType(accountType);
		accountDto.setAccountHolderName(accountHolderName);
		accountDto.setBalance(balance);
		return accountDto;
	}

	private PostPowerOfAttorneyDto createPowerOfAttorney(String accountNumber, String grantorName, String granteeName, Authorization authorization) {
		PostPowerOfAttorneyDto powerOfAttorneyDto = new PostPowerOfAttorneyDto();
		powerOfAttorneyDto.setAccountNumber(accountNumber);
		powerOfAttorneyDto.setGrantorName(grantorName);
		powerOfAttorneyDto.setGranteeName(granteeName);
		powerOfAttorneyDto.setAuthorization(authorization);
		return powerOfAttorneyDto;
	}
}
