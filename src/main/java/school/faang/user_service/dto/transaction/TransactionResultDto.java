package school.faang.user_service.dto.transaction;

import lombok.Data;

@Data
public class TransactionResultDto {
    private Long TransactionNumber;
    private String paymentStatus;
    private String message;
    private String purpose;
    private String purchaseItem;
    private Long amount;
    private String currency;
    private String status;
}
