package com.janindu.ticket.controller;

import com.janindu.ticket.model.MainConfig;
import com.janindu.ticket.model.TicketPool;
import com.janindu.ticket.service.LoggingService;
import com.janindu.ticket.service.MainService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class MainController {
    private final MainService mainService;
    private final TicketPool ticketPool;
    private final LoggingService loggingService;

    public MainController(MainService mainService, TicketPool ticketPool, LoggingService loggingService) {
        this.mainService = mainService;
        this.ticketPool = ticketPool;
        this.loggingService = loggingService;
    }

    @PostMapping("/config")
    public ResponseEntity<Map<String, Object>> configureSystem(@RequestBody @Valid MainConfig config) {
        mainService.clearConfig();
        MainConfig savedConfig = mainService.saveConfig(config);

        Map<String, Object> response = new HashMap<>();
        response.put("ticketsAvailable", savedConfig.getTotalTickets());

        loggingService.addLog("System configured with " + config.getNumberOfVendors() + " vendors and " + config.getNumberOfCustomers() + " customers.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/config")
    public ResponseEntity<MainConfig> getConfig() {
        return mainService.getConfig()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/start")
    public ResponseEntity<String> startSystem() {
        Optional<MainConfig> config = mainService.getConfig();
        if (config.isEmpty()) {
            return ResponseEntity.badRequest().body("Configuration not found. Please configure the system first.");
        }
        mainService.startSystem(config, ticketPool);
        loggingService.addLog("System started successfully.");
        return ResponseEntity.ok("System started successfully!");
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopSystem() {
        mainService.stopSystem();
        loggingService.addLog("System stopped successfully.");
        return ResponseEntity.ok("System stopped successfully!");
    }

    @GetMapping("/log")
    public ResponseEntity<List<String>> getLogs() {
        return ResponseEntity.ok(loggingService.getLogs());
    }

    @DeleteMapping("/log")
    public ResponseEntity<String> clearLogs() {
        loggingService.clearLogs();
        return ResponseEntity.ok("Logs cleared successfully!");
    }

    @GetMapping("/tickets/count")
    public ResponseEntity<Map<String, Integer>> getRealTimeTicketCount() {
        int ticketCount = ticketPool.getCurrentTicketCount(); // Assuming getCurrentTicketCount() returns an integer
        Map<String, Integer> response = new HashMap<>();
        response.put("ticketsAvailable", ticketCount);

        // Return JSON response with Content-Type: application/json
        return ResponseEntity.ok()
                .header("Content-Type", "application/json") // Ensures the response is marked as JSON
                .body(response);
    }
}
