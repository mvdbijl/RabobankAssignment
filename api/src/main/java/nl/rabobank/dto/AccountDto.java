package nl.rabobank.dto;

import nl.rabobank.account.AccountType;

import java.util.Objects;

public class AccountDto {

	private String accountHolderName;

	private String accountNumber;

	private AccountType accountType;

	private Double balance;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AccountDto)) return false;
		AccountDto that = (AccountDto) o;
		return Objects.equals(getAccountHolderName(), that.getAccountHolderName()) && Objects.equals(getAccountNumber(), that.getAccountNumber()) && getAccountType() == that.getAccountType() && Objects.equals(getBalance(), that.getBalance());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getAccountHolderName(), getAccountNumber(), getAccountType(), getBalance());
	}

	public String getAccountHolderName() {
		return accountHolderName;
	}

	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "AccountDto{" +
				"accountHolderName='" + accountHolderName + '\'' +
				", accountNumber='" + accountNumber + '\'' +
				", accountType=" + accountType +
				", balance=" + balance +
				'}';
	}
}
