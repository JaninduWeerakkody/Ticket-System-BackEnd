package com.janindu.ticket.service;

import com.janindu.ticket.model.MainConfig;
import com.janindu.ticket.model.TicketPool;
import com.janindu.ticket.repository.SystemConfigRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MainService {

    private final SystemConfigRepository configRepository; // Repository to handle system configuration
    private final ThreadService threadService; // Service to manage threads for ticket processing

    // Constructor to inject the required dependencies (configRepository and threadService)
    public MainService(SystemConfigRepository configRepository, ThreadService threadService) {
        this.configRepository = configRepository;
        this.threadService = threadService;
    }

    /**
     * Saves the configuration after clearing any existing configuration.
     * @param config the configuration to be saved
     * @return the saved configuration
     */
    public MainConfig saveConfig(MainConfig config) {
        clearConfig(); // Clear existing configuration before saving the new one
        return configRepository.save(config); // Save and return the new configuration
    }

    /**
     * Retrieves the first available configuration from the repository.
     * @return an Optional containing the configuration if available
     */
    public Optional<MainConfig> getConfig() {
        // Fetch all configurations and return the first one found, if any
        return configRepository.findAll().stream().findFirst();
    }

    /**
     * Clears all configurations in the repository.
     */
    public void clearConfig() {
        configRepository.deleteAll(); // Delete all records from the configuration repository
    }

    /**
     * Starts the system by retrieving the configuration and initializing the threads for ticket processing.
     * @param ticketPool the pool of tickets for live logging and thread operations
     * @throws IllegalStateException if no configuration is found in the repository
     */
    public void startSystem(TicketPool ticketPool) {
        Optional<MainConfig> config = getConfig(); // Get the configuration
        if (config.isEmpty()) {
            throw new IllegalStateException("No configuration found. Please configure the system first.");
        }
        // Configure the thread service with the configuration and the ticket pool
        threadService.configure(config.get(), ticketPool);
        threadService.startThreads(ticketPool); // Start the threads for ticket processing
    }

    /**
     * Stops the system by stopping all threads.
     */
    public void stopSystem() {
        threadService.stopThreads(); // Stop all threads related to ticket processing
    }
}
