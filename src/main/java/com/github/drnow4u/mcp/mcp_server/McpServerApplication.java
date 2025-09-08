package com.github.drnow4u.mcp.mcp_server;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class McpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpServerApplication.class, args);
    }

    @Bean
    ToolCallbackProvider orlenWholesalePriceProvider(OrlenWholesalePriceTools orlenWholesalePriceTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(orlenWholesalePriceTools)
                .build();
    }

}

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
                                PRODUCT: %s   |  PRICE: %s PLN | AMOUNT: 1000L | EFFECTIVE DATE: %s | DESCRIPTION POLISH: %s""".formatted(p.productName(), p.value(), p.effectiveDate(), products.get(p.productName()).name()))
                        .toArray(String[]::new);
            } else
                return new String[]{"No data found"};

        } catch (RestClientException e) {
            System.err.println("Communication error: " + e.getMessage());
            return new String[]{"Communication error: " + e.getMessage()};
        }
    }

    //    @Tool(description = "returns history wholesale prices for Orlen fuel products per given year")
//    public String[] wholesaleHistoryFuelPrices(@ToolParam(description = "Year") String year) {
//        return new String[]{
//                """
//                        PRODUCT: %s   |  PRICE: %s PLN | AMOUNT: 1000L""".formatted("ONEkodiesel", 4892)
//        };
//    }

}

@Service
class OrlenWholesalePriceService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String ORLEN_PRODUCTS_API_URL = "https://tool.orlen.pl/api/wholesalefuelprices/Products";
    private static final String ORLEN_WHOLESALE_FUEL_PRICES_API_URL = "https://tool.orlen.pl/api/wholesalefuelprices";

    public List<FuelProduct> products() {
        ResponseEntity<List<FuelProduct>> response = restTemplate.exchange(
                ORLEN_PRODUCTS_API_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getBody();
    }

    public ResponseEntity<List<WholesaleFuelPriceDto>> currentFuelPrices() {
        return restTemplate.exchange(
                ORLEN_WHOLESALE_FUEL_PRICES_API_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
    }

}

record FuelProduct(
        Integer id,
        String name,
        String nameEn,
        String symbol
) {
}

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
