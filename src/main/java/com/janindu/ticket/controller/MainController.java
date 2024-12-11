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

    // Constructor to inject the services
    public MainController(MainService mainService, TicketPool ticketPool, LoggingService loggingService) {
        this.mainService = mainService;
        this.ticketPool = ticketPool;
        this.loggingService = loggingService;
    }

    /**
     * Configures the system by accepting a MainConfig object.
     * Clears any existing configuration, saves the new one, and returns a response with tickets available.
     * @param config The configuration to be saved
     * @return ResponseEntity containing the available tickets
     */
    @PostMapping("/config")
    public ResponseEntity<Map<String, Object>> configureSystem(@RequestBody @Valid MainConfig config) {
        mainService.clearConfig();
        MainConfig savedConfig = mainService.saveConfig(config);
        Map<String, Object> response = new HashMap<>();
        response.put("ticketsAvailable", savedConfig.getTotalTickets());

        loggingService.addLog("System configured with " + config.getNumberOfVendors() + " vendors and " + config.getNumberOfCustomers() + " customers.");

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the current configuration of the system.
     * @return ResponseEntity containing the current configuration or a 404 if not found
     */
    @GetMapping("/config")
    public ResponseEntity<MainConfig> getConfig() {
        return mainService.getConfig()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Starts the system if a configuration is available.
     * @return ResponseEntity with a message indicating success or failure
     */
    @PostMapping("/start")
    public ResponseEntity<String> startSystem() {
        Optional<MainConfig> config = mainService.getConfig();

        if (config.isEmpty()) {
            return ResponseEntity.badRequest().body("Configuration not found. Please configure the system first.");
        }

        mainService.startSystem(ticketPool);
        loggingService.addLog("System started successfully.");

        // Return a success message
        return ResponseEntity.ok("System started successfully!");
    }

    /**
     * Stops the system and logs the action.
     * @return ResponseEntity with a success message
     */
    @PostMapping("/stop")
    public ResponseEntity<String> stopSystem() {
        mainService.stopSystem(); // Stop the system
        loggingService.addLog("System stopped successfully.");

        return ResponseEntity.ok("System stopped successfully!");
    }

    /**
     * Retrieves the logs of the system.
     * @return ResponseEntity containing the list of logs
     */
    @GetMapping("/log")
    public ResponseEntity<List<String>> getLogs() {
        return ResponseEntity.ok(loggingService.getLogs());
    }

    /**
     * Clears the system logs.
     * @return ResponseEntity indicating that the logs were cleared successfully
     */
    @DeleteMapping("/log")
    public ResponseEntity<String> clearLogs() {
        loggingService.clearLogs();
        return ResponseEntity.ok("Logs cleared successfully!");
    }

    /**
     * Retrieves the real-time ticket count from the ticket pool.
     * @return ResponseEntity containing the current ticket count
     */
    @GetMapping("/tickets/count")
    public ResponseEntity<Map<String, Integer>> getRealTimeTicketCount() {
        int ticketCount = ticketPool.getCurrentTicketCount();

        Map<String, Integer> response = new HashMap<>();
        response.put("ticketsAvailable", ticketCount);

        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(response);
    }
}
