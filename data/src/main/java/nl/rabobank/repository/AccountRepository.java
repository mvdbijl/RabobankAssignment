package nl.rabobank.repository;

import nl.rabobank.account.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository<Account, Integer> {
	Optional<Account> findByAccountNumber(String accountNumber);
}
