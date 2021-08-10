package nl.rabobank.account;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class SavingsAccount implements Account {

	private String accountHolderName;

	private String accountNumber;

	private Double balance;

}
