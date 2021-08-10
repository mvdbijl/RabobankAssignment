package nl.rabobank.dto;

import nl.rabobank.authorizations.Authorization;

import java.util.Objects;

public class PostPowerOfAttorneyDto {

	private String accountNumber;

	private Authorization authorization;

	private String granteeName;

	private String grantorName;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PostPowerOfAttorneyDto)) return false;
		PostPowerOfAttorneyDto that = (PostPowerOfAttorneyDto) o;
		return Objects.equals(getAccountNumber(), that.getAccountNumber()) && getAuthorization() == that.getAuthorization() && Objects.equals(getGranteeName(), that.getGranteeName()) && Objects.equals(getGrantorName(), that.getGrantorName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getAccountNumber(), getAuthorization(), getGranteeName(), getGrantorName());
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Authorization getAuthorization() {
		return authorization;
	}

	public void setAuthorization(Authorization authorization) {
		this.authorization = authorization;
	}

	public String getGranteeName() {
		return granteeName;
	}

	public void setGranteeName(String granteeName) {
		this.granteeName = granteeName;
	}

	public String getGrantorName() {
		return grantorName;
	}

	public void setGrantorName(String grantorName) {
		this.grantorName = grantorName;
	}

	@Override
	public String toString() {
		return "PostPowerOfAttorneyDto{" +
				"accountNumber='" + accountNumber + '\'' +
				", authorization=" + authorization +
				", granteeName='" + granteeName + '\'' +
				", grantorName='" + grantorName + '\'' +
				'}';
	}
}
