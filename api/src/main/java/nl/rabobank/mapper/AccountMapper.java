package nl.rabobank.mapper;

import nl.rabobank.account.Account;
import nl.rabobank.account.AccountType;
import nl.rabobank.account.PaymentAccount;
import nl.rabobank.account.SavingsAccount;
import nl.rabobank.dto.AccountDto;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public abstract class AccountMapper {

	@Named("toAccount")
	public Account toAccount(AccountDto accountDto) {
		if (accountDto.getAccountType() == AccountType.SAVINGS) {
			return toSavingsAccount(accountDto);
		}
		if (accountDto.getAccountType() == AccountType.PAYMENT) {
			return toPaymentAccount(accountDto);
		}
		throw new IllegalArgumentException("Illegal AccountType passed");
	}

	public abstract PaymentAccount toPaymentAccount(AccountDto accountDto);

	public abstract SavingsAccount toSavingsAccount(AccountDto accountDto);
}
