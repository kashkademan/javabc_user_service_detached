package school.faang.user_service.service.transaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.transaction.TransactionResultDto;
import school.faang.user_service.dto.transaction.UpdateTransactionDto;
import school.faang.user_service.entity.transaction.Transaction;
import school.faang.user_service.entity.transaction.Payable;
import school.faang.user_service.entity.transaction.TransactionStatus;
import school.faang.user_service.mapper.TransactionMapper;
import school.faang.user_service.repository.TransactionRepository;
import school.faang.user_service.service.payment.PaymentService;

import school.faang.user_service.service.utils.TransactionServiceUtils;

import java.util.Currency;
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
    private UserContext userContext;
    @Spy
    private TransactionMapper transactionMapper;
    @InjectMocks
    private TransactionService transactionService;

    @Test
    void buyItemSuccess() {
        // 1. Arrange: Определяем тестовые данные и поведение моков/шпионов
        Long userId = 1L;
        Long transactionNumber = 67890L; // Используем конкретный номер транзакции
        Currency currencyCode = Currency.getInstance("EUR"); // Код валюты для проверки

        Payable mockItem = mock(Payable.class); // Входящий объект для покупки

        // Объект транзакции, который будет проходить через весь процесс
        Transaction transaction = new Transaction();
        transaction.setTransactionNumber(transactionNumber); // Важно для поиска транзакции
        transaction.setCurrencyCode(currencyCode);         // Важно для обогащения финального DTO

        // DTO, участвующие в процессе
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(); // Результат маппинга transactionMapper.toPaymentRequestDto
        PaymentResponseDto paymentResponseDto = new PaymentResponseDto(); // Результат paymentService.buyItem
        // paymentResponseDto должен содержать paymentNumber для корректной работы дальнейших шагов
        paymentResponseDto.setPaymentNumber(transactionNumber);

        UpdateTransactionDto updateTransactionDto = new UpdateTransactionDto(); // Результат transactionMapper.toUpdateTransactionDto
        updateTransactionDto.setPaymentNumber(transactionNumber);
        updateTransactionDto.setTransactionStatus("SUCCESS"); // Для проверки успешного сценария в updateTransaction

        TransactionResultDto expectedResultDto = new TransactionResultDto(); // Ожидаемый финальный результат от transactionMapper.toDto

        // --- Стаббинг зависимостей ---

        // UserContext
        doNothing().when(userContext).setUserId(userId);  // userContext.setUserId(userId) вызывается в начале

        // Логика создания транзакции (внутри private метода createTransaction)
        when(transactionServiceUtils.buildTransaction(userId, mockItem)).thenReturn(transaction); //
        when(transactionRepository.save(transaction)).thenReturn(transaction); //

        // Мапперы (TransactionMapper является @Spy)
        // Стаббим методы шпиона для изоляции и контроля
        when(transactionMapper.toPaymentRequestDto(transaction)).thenReturn(paymentRequestDto); //
        when(transactionMapper.toUpdateTransactionDto(paymentResponseDto)).thenReturn(updateTransactionDto); //
        // Сервис вызывает transactionMapper.toDto, а затем устанавливает валюту
        when(transactionMapper.toDto(transaction)).thenReturn(expectedResultDto);


        // PaymentService
        when(paymentService.buyItem(paymentRequestDto)).thenReturn(paymentResponseDto); //

        // Логика обновления транзакции (внутри private метода updateTransaction)
        when(transactionRepository.findTransactionByTransactionNumber(transactionNumber))
                .thenReturn(Optional.of(transaction)); //

        // 2. Act: Вызываем тестируемый метод
        TransactionResultDto actualResultDto = transactionService.buyItem(userId, mockItem);

        // 3. Assert: Проверяем результат и состояние
        assertNotNull(actualResultDto, "Результат не должен быть null");
        // Сервис модифицирует DTO, возвращенный transactionMapper.toDto(), устанавливая валюту.
        // expectedResultDto (который и есть actualResultDto по ссылке из-за стаббинга) будет изменен.
        assertSame(expectedResultDto, actualResultDto, "Возвращенный DTO должен быть тем же объектом, что и настроенный в стабе маппера.");
        assertEquals(currencyCode, actualResultDto.getCurrency(), "Код валюты должен быть установлен в финальном DTO.");


        // --- Верификация вызовов методов на моках и шпионах ---
        verify(userContext).setUserId(userId);
        verify(transactionServiceUtils).buildTransaction(userId, mockItem);
        verify(transactionRepository).save(transaction);
        verify(transactionMapper).toPaymentRequestDto(transaction);
        verify(paymentService).buyItem(paymentRequestDto);
        verify(transactionMapper).toUpdateTransactionDto(paymentResponseDto);
        verify(transactionRepository).findTransactionByTransactionNumber(transactionNumber);

        // Проверка изменения состояния объекта транзакции
        assertEquals(TransactionStatus.SETTLED, transaction.getTransactionStatus(), "Статус транзакции должен быть SETTLED."); //

        // Верификация вызова метода transactionMapper.updateTransactionFromDto (void метод на шпионе)
        // Это гарантирует, что реальный метод шпиона был вызван (т.к. он не застаблен отдельно)
        verify(transactionMapper).updateTransactionFromDto(updateTransactionDto, transaction); //

        // Верификация вызова transactionMapper.toDto
        verify(transactionMapper).toDto(transaction); //
    }
}