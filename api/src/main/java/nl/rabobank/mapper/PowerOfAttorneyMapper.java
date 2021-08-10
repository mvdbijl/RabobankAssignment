package nl.rabobank.mapper;

import nl.rabobank.account.Account;
import nl.rabobank.authorizations.PowerOfAttorney;
import nl.rabobank.dto.PostPowerOfAttorneyDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {AccountMapper.class})
public abstract class PowerOfAttorneyMapper {

	@Mapping(target = "account")
	public abstract PowerOfAttorney toPowerOfAttorney(Account account, PostPowerOfAttorneyDto postPowerOfAttorneyDto);
}
