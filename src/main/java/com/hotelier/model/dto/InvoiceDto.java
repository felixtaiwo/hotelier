package com.hotelier.model.dto;

import com.hotelier.model.entity.Invoice;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Setter @Getter @NotNull
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceDto {
    public static List<InvoiceItemDto> getInvoice(List<Invoice> invoices) {
        if (invoices == null){
            return null;
        }
        return invoices.stream().map(InvoiceItemDto::fromInvoiceItem).collect(Collectors.toList());
    }
    @Setter @Getter @NotNull
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class InvoiceItemDto {
        String desc;
        double unitCost;
        double quantity;
        double total;
        LocalDateTime dateTime;

        public static InvoiceItemDto fromInvoiceItem(Invoice invoiceItem) {
            InvoiceItemDto invoiceItemDto = new InvoiceItemDto();
            invoiceItemDto.setDesc(invoiceItem.getDescription());
            invoiceItemDto.setQuantity(invoiceItem.getQuantity());
            invoiceItemDto.setTotal(invoiceItem.getPrice());
            invoiceItemDto.setUnitCost(invoiceItem.getUnitPrice());
            invoiceItemDto.setDateTime(invoiceItem.getCreateDate());
            return invoiceItemDto;
        }
    }
}
