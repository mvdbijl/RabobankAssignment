package nl.rabobank.repository;

import nl.rabobank.account.Account;
import nl.rabobank.authorizations.Authorization;
import nl.rabobank.authorizations.PowerOfAttorney;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PowerOfAttorneyRepository extends MongoRepository<PowerOfAttorney, String> {
	List<PowerOfAttorney> findAllByGranteeName(String granteeName);

	Optional<Object> findByAccountAndGranteeNameAndAuthorization(Account account, String granteeName, Authorization authorization);
}
