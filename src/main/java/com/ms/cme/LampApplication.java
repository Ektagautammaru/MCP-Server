package com.ms.cme;

import com.ms.cme.mcpserver.MCPServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LampApplication {

	public static void main(String[] args) {
		MCPServer.main(args);

	}

}
