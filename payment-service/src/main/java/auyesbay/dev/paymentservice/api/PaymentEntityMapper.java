package auyesbay.dev.paymentservice.api;

import auyesbay.dev.paymentservice.domain.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PaymentEntityMapper {
    PaymentEntity toEntity(CreatePaymentRequestDto createPaymentRequestDto);

    @Mapping(source = "id", target = "paymentId")
    CreatePaymentResponseDto toResponseDto(PaymentEntity paymentEntity);
}
