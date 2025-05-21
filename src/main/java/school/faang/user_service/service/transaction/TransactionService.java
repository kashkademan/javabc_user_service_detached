package school.faang.user_service.service.transaction;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.transaction.TransactionResultDto;
import school.faang.user_service.dto.transaction.UpdateTransactionDto;
import school.faang.user_service.entity.transaction.Transaction;
import school.faang.user_service.entity.transaction.Payable;
import school.faang.user_service.mapper.TransactionMapper;
import school.faang.user_service.repository.TransactionRepository;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.service.utils.TransactionServiceUtils;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionServiceUtils transactionServiceUtils;
    private final PaymentService paymentService;
    private final TransactionMapper transactionMapper;

    private Transaction createTransaction(Long userId, Payable item) {
        Transaction transaction = transactionServiceUtils.buildTransaction(userId, item);
        transactionRepository.save(transaction);
        return transaction;
    }

    public TransactionResultDto buyItem(Long userId, Payable item) {
        return updateTransaction(
                transactionMapper.toUpdateTransactionDto(
                        paymentService.buyItem(
                                transactionMapper.toPaymentRequestDto(
                                        createTransaction(userId, item)))
                ));
    }

    private TransactionResultDto updateTransaction(UpdateTransactionDto updateTransaction) {
        Long transactionNumber = updateTransaction.getPaymentNumber();
        Transaction transaction = transactionRepository.findTransactionByTransactionNumber(transactionNumber)
                .orElseThrow(() -> new EntityNotFoundException("Transaction with number " + transactionNumber + " not found!"));
        transactionMapper.updateTransactionFromDto(updateTransaction, transaction);
        return transactionMapper.toDto(transaction);
    }
}
