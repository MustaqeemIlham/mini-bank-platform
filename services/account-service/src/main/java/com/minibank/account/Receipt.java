package com.minibank.account;

import java.math.BigDecimal;
import java.time.Instant;

// Proof of a finished transfer. Returned to the caller and saved to S3 as JSON.
public record Receipt(String transferId, Long fromId, Long toId, BigDecimal amount, Instant time) {
}
