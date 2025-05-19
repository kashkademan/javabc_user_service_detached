package school.faang.user_service.mapper;

import jakarta.persistence.Column;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.transaction.TransactionResultDto;
import school.faang.user_service.dto.transaction.UpdateTransactionDto;
import school.faang.user_service.entity.transaction.Transaction;
import school.faang.user_service.entity.transaction.TransactionStatus;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    PaymentRequestDto toPaymentRequestDto(Transaction transaction);

    @Mapping(target = "transactionStatus", source = "transactionStatus")
    @Mapping(target = "verificationCode", source = "verificationCode")
    @Mapping(target = "message", source = "message")
    void updateTransactionFromDto(UpdateTransactionDto updateTransactionDto, @MappingTarget Transaction transaction);
    UpdateTransactionDto toUpdateTransactionDto(PaymentResponseDto response);
    TransactionResultDto toDto(Transaction transaction);

    default TransactionStatus mapTransactionStatus(String status) {
        return TransactionStatus.valueOf(status.toUpperCase());
    }
}
