package com.github.drnow4u.mcp.mcp_server;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@RegisterReflectionForBinding({FuelProduct.class, WholesaleFuelPriceDto.class})
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
