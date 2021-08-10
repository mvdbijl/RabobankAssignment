package nl.rabobank.service;

import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.repository.PowerOfAttorneyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PowerOfAttorneyDataService {

	PowerOfAttorneyRepository powerOfAttorneyRepository;

	public PowerOfAttorneyDataService(PowerOfAttorneyRepository powerOfAttorneyRepository) {
		this.powerOfAttorneyRepository = powerOfAttorneyRepository;
	}

	public List<PowerOfAttorney> getPowerOfAttorneyForGrantee(String granteeName) {
		return powerOfAttorneyRepository.findAllByGranteeName(granteeName);
	}

	public void save(PowerOfAttorney powerOfAttorney) {
		if (powerOfAttorneyRepository.findByAccountAndGranteeNameAndAuthorization(
				powerOfAttorney.getAccount(),
				powerOfAttorney.getGranteeName(),
				powerOfAttorney.getAuthorization())
				.isPresent()) {
			throw new IllegalArgumentException("Power Of Attorney for this account, grantee and authorization already exists");
		}
		powerOfAttorneyRepository.save(powerOfAttorney);
	}
}
