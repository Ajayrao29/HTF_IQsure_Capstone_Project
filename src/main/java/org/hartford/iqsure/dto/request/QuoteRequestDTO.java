package org.hartford.iqsure.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class QuoteRequestDTO {
    private BigDecimal quoteAmount;
    private String remarks;
}
