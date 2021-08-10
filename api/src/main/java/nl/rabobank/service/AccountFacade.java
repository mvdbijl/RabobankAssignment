package nl.rabobank.service;

import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.dto.AccountDto;
import nl.rabobank.dto.PostPowerOfAttorneyDto;
import nl.rabobank.mapper.AccountMapper;
import nl.rabobank.mapper.PowerOfAttorneyMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AccountFacade {

	private final AccountDataService accountDataService;

	private final AccountMapper accountMapper;

	private final PowerOfAttorneyDataService powerOfAttorneyDataService;

	private final PowerOfAttorneyMapper powerOfAttorneyMapper;

	public AccountFacade(PowerOfAttorneyMapper powerOfAttorneyMapper,
						 AccountDataService accountDataService,
						 PowerOfAttorneyDataService powerOfAttorneyDataService,
						 AccountMapper accountMapper) {
		this.powerOfAttorneyMapper = powerOfAttorneyMapper;
		this.accountDataService = accountDataService;
		this.powerOfAttorneyDataService = powerOfAttorneyDataService;
		this.accountMapper = accountMapper;
	}

	public void createAccount(AccountDto accountDto) {
		try {
			var account = accountMapper.toAccount(accountDto);
			accountDataService.save(account);
		} catch (RuntimeException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
		}
	}

	public void createPowerOfAttorney(PostPowerOfAttorneyDto postPowerOfAttorneyDto) {
		var account = accountDataService.findByAccountNumber(postPowerOfAttorneyDto.getAccountNumber());

		if (account == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account does not exist");
		}
		if (!account.getAccountHolderName().equals(postPowerOfAttorneyDto.getGrantorName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "grantorName is not the account holder");
		}

		var powerOfAttorney = powerOfAttorneyMapper.toPowerOfAttorney(account, postPowerOfAttorneyDto);

		try {
			powerOfAttorneyDataService.save(powerOfAttorney);
		} catch (RuntimeException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
		}
	}

	public List<PowerOfAttorney> getPowerOfAttorneyForGrantee(String granteeName) {
		return powerOfAttorneyDataService.getPowerOfAttorneyForGrantee(granteeName);
	}
}
