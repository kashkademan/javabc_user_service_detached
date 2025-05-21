package school.faang.user_service.service.transaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.transaction.TransactionResultDto;
import school.faang.user_service.dto.transaction.UpdateTransactionDto;
import school.faang.user_service.entity.transaction.Transaction;
import school.faang.user_service.entity.transaction.Payable;
import school.faang.user_service.mapper.TransactionMapper;
import school.faang.user_service.repository.TransactionRepository;
import school.faang.user_service.service.payment.PaymentService;

import school.faang.user_service.service.utils.TransactionServiceUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionServiceUtils transactionServiceUtils;
    @Mock
    private PaymentService paymentService;
    @Mock
    private TransactionMapper transactionMapper;
    @InjectMocks
    private TransactionService transactionService;

    @Test
    void buyItemSuccess() {
        Long userId = 1L;
        Payable mockItem = mock(Payable.class);
        Transaction mockTransaction = new Transaction();
        PaymentRequestDto mockPaymentRequestDto = new PaymentRequestDto();
        PaymentResponseDto mockPaymentResponseDto = new PaymentResponseDto();
        UpdateTransactionDto mockUpdateTransactionDto = new UpdateTransactionDto();
        TransactionResultDto expectedResult = new TransactionResultDto();

        when(transactionServiceUtils.buildTransaction(userId, mockItem)).thenReturn(mockTransaction);
        when(transactionRepository.save(any())).thenReturn(mockTransaction);
        when(transactionMapper.toPaymentRequestDto(any())).thenReturn(mockPaymentRequestDto);
        when(paymentService.buyItem(any())).thenReturn(mockPaymentResponseDto);
        when(transactionMapper.toUpdateTransactionDto(any())).thenReturn(mockUpdateTransactionDto);
        when(transactionRepository.findTransactionByTransactionNumber(any())).thenReturn(Optional.of(mockTransaction));
        when(transactionMapper.toDto(any())).thenReturn(expectedResult);

        TransactionResultDto result = transactionService.buyItem(userId, mockItem);

        assertNotNull(result);
        verify(transactionServiceUtils).buildTransaction(userId, mockItem);
        verify(transactionRepository).save(mockTransaction);
    }


}