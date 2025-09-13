package com.github.drnow4u.mcp.mcp_server;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

record WholesaleFuelPriceDto(
        @JsonProperty("productName")
        String productName,

        @JsonProperty("effectiveDate")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime effectiveDate,

        @JsonProperty("publishFrom")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime publishFrom,

        @JsonProperty("value")
        BigDecimal value,

        @JsonProperty("locationName")
        String locationName,

        @JsonProperty("locationSymbol")
        String locationSymbol,

        @JsonProperty("unit")
        String unit
) {
}
