package com.ms.cme.mcpserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MCPServer {

    public static void main(String[] args)  {

        // ---------------- Transport ----------------
        var jsonMapper = new JacksonMcpJsonMapper(new ObjectMapper());
        var transportProvider = new StdioServerTransportProvider(jsonMapper);

        // ---------------- Server ----------------
        McpSyncServer syncServer = McpServer.sync(transportProvider)
                .serverInfo("my-server", "1.0.0")
                .capabilities(
                        McpSchema.ServerCapabilities.builder()
                                .tools(true)
                                .logging()
                                .build()
                )
                .build();

        // ---------------- Input Schema ----------------
        Map<String, Object> inputProperties = Map.of(
                "operation", Map.of("type", "string"),
                "a", Map.of("type", "number"),
                "b", Map.of("type", "number")
        );

        McpSchema.JsonSchema EMPTY_JSON_SCHEMA = new McpSchema.JsonSchema("object",
                inputProperties,
                null,
                null,
                null,
                null
        );


        McpSchema.Tool newTool = McpSchema.Tool.builder()
                .name("new-tool")
                .title("New test tool")
                .inputSchema(EMPTY_JSON_SCHEMA)
                .build();

        // ---------------- Tool Implementation ----------------
        McpServerFeatures.SyncToolSpecification calculatorSpec =
                new McpServerFeatures.SyncToolSpecification(
                        newTool,
                        (exchange, arguments) -> {

                            return new McpSchema.CallToolResult(
                                    String.valueOf(Map.of("result", "Run MCP Tool Successfully")),
                                    false
                            );
                        }
                );

        // ---------------- Register Tool ----------------
        syncServer.addTool(calculatorSpec);

    }
}
