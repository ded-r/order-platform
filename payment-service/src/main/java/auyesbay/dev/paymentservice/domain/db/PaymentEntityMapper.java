package auyesbay.dev.paymentservice.domain.db;

import auyesbay.dev.commonlibs.http.payment.CreatePaymentRequestDto;
import auyesbay.dev.commonlibs.http.payment.CreatePaymentResponseDto;
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
