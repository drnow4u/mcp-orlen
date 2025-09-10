package com.github.drnow4u.mcp.mcp_server;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Service
class OrlenWholesalePriceService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String ORLEN_PRODUCTS_API_URL = "https://tool.orlen.pl/api/wholesalefuelprices/Products";
    private static final String ORLEN_WHOLESALE_FUEL_PRICES_API_URL = "https://tool.orlen.pl/api/wholesalefuelprices";
    // https://tool.orlen.pl/api/wholesalefuelprices/ByProduct?productId=41&from=2025-01-01&to=2025-09-06
    private static final String ORLEN_WHOLESALE_FUEL_PRICES_BY_DATE_API_URL = "https://tool.orlen.pl/api/wholesalefuelprices/ByProduct";

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

    public List<WholesaleFuelPriceDto> fuelPricesByDate(Integer id, LocalDate from, LocalDate to) {

        var response = restTemplate.exchange(
                ORLEN_WHOLESALE_FUEL_PRICES_BY_DATE_API_URL + "?productId={id}&from={from}&to={to}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<WholesaleFuelPriceDto>>() {
                },
                id,
                from,
                to
        );
        return response.getBody();
    }

}
