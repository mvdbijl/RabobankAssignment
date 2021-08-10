package nl.rabobank.controller;

import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.dto.PostPowerOfAttorneyDto;
import nl.rabobank.service.AccountFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/poa")
public class PowerOfAttorneyController {

	private static final Logger log = LoggerFactory.getLogger(PowerOfAttorneyController.class);

	private final AccountFacade accountFacade;

	public PowerOfAttorneyController(AccountFacade accountFacade) {
		this.accountFacade = accountFacade;
	}

	@PostMapping(value = "/create")
	@ResponseStatus(HttpStatus.CREATED)
	public void createPowerOfAttorney(@RequestBody PostPowerOfAttorneyDto postPowerOfAttorneyDto) {
		log.debug("Received createPowerOfAttorney request: {}", postPowerOfAttorneyDto);
		accountFacade.createPowerOfAttorney(postPowerOfAttorneyDto);
	}

	@GetMapping(value = "/list")
	public List<PowerOfAttorney> getPowerOfAttorneyForGrantee(@RequestParam String granteeName) {
		log.debug("Received getPowerOfAttorney request: {}", granteeName);
		return accountFacade.getPowerOfAttorneyForGrantee(granteeName);
	}
}
