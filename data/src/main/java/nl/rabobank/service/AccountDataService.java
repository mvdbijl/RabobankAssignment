package nl.rabobank.service;

import nl.rabobank.account.Account;
import nl.rabobank.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountDataService {

	AccountRepository accountRepository;

	public AccountDataService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public Account findByAccountNumber(String accountNumber) {
		Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountNumber);
		return optionalAccount.orElse(null);
	}

	public void save(Account account) {
		if (accountRepository.findByAccountNumber(account.getAccountNumber()).isPresent()) {
			throw new IllegalArgumentException("Account number already exists");
		}
		accountRepository.save(account);
	}
}
