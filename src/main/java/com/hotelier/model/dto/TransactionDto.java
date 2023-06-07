package com.hotelier.model.dto;

import com.hotelier.exception.Hotelier;
import com.hotelier.model.entity.Transaction;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@Setter @Getter @NotNull
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDto {

    enum TransactionStatus{
        SUCCESSFUL,
        INITIATED,
        UNPROCESSED,
        FAILED
    }
    public enum PaymentOption{
        CASH
    }

    List<InvoiceDto.InvoiceItemDto> invoice;
    private Long id;
    private double fee;
    private double amount;
    private double total;
    private TransactionStatus status;
    private PaymentOption channel;
    private String referenceId;
    private String paymentId;

    public static TransactionDto fromTransaction (Transaction transaction){
        if(transaction == null)
            return null;
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setId(transaction.getId());
        transactionDto.setAmount(transaction.getAmount());
        transactionDto.setFee(transaction.getFee());
        transactionDto.setReferenceId(transaction.getReferenceId());
        transactionDto.setChannel(transaction.getChannel());
        transactionDto.setTotal(transaction.getTotal());
        transactionDto.setStatus(getStatus(transaction.getStatus()));
        transactionDto.setInvoice(InvoiceDto.getInvoice(transaction.getInvoice()));
        return transactionDto;
    }

    private static TransactionStatus getStatus(int status) {
        switch (status){
            case 1:
                return TransactionStatus.SUCCESSFUL;
            case 0:
                return TransactionStatus.INITIATED;
            case -1:
                return TransactionStatus.FAILED;
            case -2:
                return TransactionStatus.UNPROCESSED;
            default:
                throw new Hotelier(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown transaction status");
        }
    }

}
