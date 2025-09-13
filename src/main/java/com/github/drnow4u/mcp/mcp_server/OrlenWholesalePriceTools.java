package com.github.drnow4u.mcp.mcp_server;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.List;

@Component
class OrlenWholesalePriceTools {
    private final OrlenWholesalePriceService orlenWholesalePriceService;

    OrlenWholesalePriceTools(OrlenWholesalePriceService orlenWholesalePriceService) {
        this.orlenWholesalePriceService = orlenWholesalePriceService;
    }

    @Tool(description = "returns list of wholesale fuels for Orlen")
    public String[] listWholesaleFuelProducts() {
        return orlenWholesalePriceService.products()
                .stream()
                .map(p -> """
                        ID: %s | NAME POLISH: %s | NAME ENGLISH: %s | SYMBOL: %s""".formatted(p.id(), p.name(), p.nameEn(), p.symbol()))
                .toArray(String[]::new);
    }

    @Tool(description = "returns current wholesale prices for Orlen fuel products")
    public String[] wholesaleCurrentFuelPrices() {
        try {

            final var products = orlenWholesalePriceService.products()
                    .stream()
                    .collect(java.util.stream.Collectors.toMap(p -> p.symbol(), p -> p));

            final ResponseEntity<List<WholesaleFuelPriceDto>> response = orlenWholesalePriceService.currentFuelPrices();

            if (response.getBody() != null) {
                return response.getBody().stream()
                        .map(p -> """
                                PRODUCT: %s | PRICE: %s PLN | AMOUNT: 1000L | EFFECTIVE DATE: %s | DESCRIPTION POLISH: %s""".formatted(p.productName(), p.value(), p.effectiveDate(), products.get(p.productName()).name()))
                        .toArray(String[]::new);
            } else
                return new String[]{"No data found"};

        } catch (RestClientException e) {
            System.err.println("Communication error: " + e.getMessage());
            return new String[]{"Communication error: " + e.getMessage()};
        }
    }

    @Tool(description = "returns history wholesale prices for Orlen fuel products per given year")
    public String[] wholesaleHistoryFuelPrices(@ToolParam(description = "Fuel ID one of listWholesaleFuelProducts") Integer id,
                                               @ToolParam(description = "From date in ISO-8601 format") String from,
                                               @ToolParam(description = "To date in ISO-8601 format") String to) {
        return orlenWholesalePriceService.fuelPricesByDate(id, LocalDate.parse(from), LocalDate.parse(to))
                .stream()
                .map(p -> "PRODUCT: %s | PRICE: %s PLN | AMOUNT: 1000L | EFFECTIVE DATE: %s".formatted(p.productName(), p.value(), p.effectiveDate()))
                .toArray(String[]::new);
    }

}
