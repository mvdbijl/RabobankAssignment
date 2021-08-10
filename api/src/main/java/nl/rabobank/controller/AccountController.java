package nl.rabobank.controller;

import nl.rabobank.dto.AccountDto;
import nl.rabobank.service.AccountFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/account")
public class AccountController {

	private static final Logger log = LoggerFactory.getLogger(AccountController.class);

	private final AccountFacade accountFacade;

	public AccountController(AccountFacade accountFacade) {
		this.accountFacade = accountFacade;
	}

	@PostMapping(value = "/create")
	@ResponseStatus(HttpStatus.CREATED)
	public void createAccount(@RequestBody AccountDto accountDto) {
		log.debug("Received createAccount request: {}", accountDto);
		accountFacade.createAccount(accountDto);
	}
}
