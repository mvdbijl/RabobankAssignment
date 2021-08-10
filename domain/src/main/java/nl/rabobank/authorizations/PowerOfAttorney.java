package nl.rabobank.authorizations;

import lombok.Builder;
import lombok.Value;
import nl.rabobank.account.Account;

@Value
@Builder(toBuilder = true)
public class PowerOfAttorney {
	Account account;

	Authorization authorization;

	String granteeName;

	String grantorName;
}
