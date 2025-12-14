package com.ms.cme.mcpserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

public class MCPServer {



    public static void main(String[] args) {

//        System.out.println("In main");

        // ---------------- Transport ----------------
        var jsonMapper = new JacksonMcpJsonMapper(new ObjectMapper());
        var transportProvider = new StdioServerTransportProvider(jsonMapper);

//        System.out.println("Transport ");

        // ---------------- Server ----------------
        McpSyncServer syncServer = McpServer.sync(transportProvider)
                .serverInfo("my-server", "1.0.0")
                .capabilities(
                        McpSchema.ServerCapabilities.builder()
                                .resources(false, true)
                                .tools(true)
                                .prompts(true)
                                .logging()
                                .completions()
                                .build()
                )
                .build();
//        System.out.println("after server");

        // ---------------- Tool Schema ----------------
        Map<String, Object> properties = Map.of(
                "operation", Map.of("type", "string"),
                "a", Map.of("type", "number"),
                "b", Map.of("type", "number")
        );

//        System.out.println("Tool schema");

        McpSchema.JsonSchema inputSchema = new McpSchema.JsonSchema(
                "object",                           // type
                properties,                         // properties
                List.of("operation", "a", "b"),     // required
                false,                              // additionalProperties
                Map.of(),                           // definitions
                Map.of()                            // extra
        );

        // ---------------- Tool ----------------
        McpSchema.Tool calculatorTool = new McpSchema.Tool(
                "calculator",               // name
                "Calculator Tool",          // title
                "Basic calculator",         // description
                inputSchema,                // input schema
                Map.of(),                   // metadata
                null,                       // annotations
                Map.of()                    // extra
        );

        // ---------------- Tool Implementation ----------------
        McpServerFeatures.SyncToolSpecification calculatorSpec =
                new McpServerFeatures.SyncToolSpecification(
                        calculatorTool,
                        (exchange, arguments) ->
                                new McpSchema.CallToolResult(
                                        "Calculator tool executed successfully",
                                        false
                                )
                );

        // Register tool
        syncServer.addTool(calculatorSpec);

        // Close server (for demo only)
        syncServer.close();
    }
}
