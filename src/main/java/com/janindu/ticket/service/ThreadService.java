package com.janindu.ticket.service;

import com.janindu.ticket.model.MainConfig;
import com.janindu.ticket.model.TicketPool;
import com.janindu.ticket.model.Customer;
import com.janindu.ticket.model.Vendor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service class responsible for managing threads related to Vendors and Customers,
 * as well as logging the live ticket count.
 */
@Service
public class ThreadService {

    private final LoggingService loggingService;
    private List<Thread> threads = new ArrayList<>();
    private volatile boolean running = false; // Indicates if threads are running
    private ScheduledExecutorService liveCountLogger;
    private final int LOG_INTERVAL = 1; // Log interval in seconds

    /**
     * Constructor to initialize the ThreadService with a LoggingService dependency.
     *
     * @param loggingService Service to handle logging operations.
     */
    public ThreadService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    /**
     * Configures the system by setting up Vendor and Customer threads based on the provided configuration.
     *
     * @param config     The main configuration object containing the number of vendors and customers.
     * @param ticketPool The shared TicketPool resource for Vendors and Customers.
     */
    public void configure(MainConfig config, TicketPool ticketPool) {
        threads.clear(); // Clear any existing threads
        createVendorThreads(config, ticketPool); // Create Vendor threads
        createCustomerThreads(config, ticketPool); // Create Customer threads

        logMessage("System configured with " + config.getNumberOfVendors() + " vendors and " + config.getNumberOfCustomers() + " customers.");
    }

    /**
     * Starts all configured threads and the live ticket count logger.
     *
     * @param ticketPool The shared TicketPool resource.
     * @throws IllegalStateException if threads are already running.
     */
    public void startThreads(TicketPool ticketPool) {
        if (running) {
            throw new IllegalStateException("Threads are already running.");
        }
        running = true;

        // Start Vendor and Customer threads
        threads.forEach(Thread::start);

        // Start live ticket count logger
        startLiveCountLogger(ticketPool);

        logMessage("All threads started.");
    }

    /**
     * Stops all running threads and the live ticket count logger.
     *
     * @throws IllegalStateException if threads are not currently running.
     */
    public void stopThreads() {
        if (!running) {
            throw new IllegalStateException("Threads are not running.");
        }

        // Interrupt all threads
        threads.forEach(Thread::interrupt);

        // Stop the live count logger
        stopLiveCountLogger();

        running = false;
        logMessage("All threads stopped.");
    }

    /**
     * Creates Vendor threads based on the configuration.
     *
     * @param config     The main configuration object containing the number of vendors.
     * @param ticketPool The shared TicketPool resource.
     */
    private void createVendorThreads(MainConfig config, TicketPool ticketPool) {
        for (int i = 0; i < config.getNumberOfVendors(); i++) {
            threads.add(new Thread(
                    new Vendor(ticketPool, config.getTicketReleaseRate(), config.getMaxTicketCapacity(), loggingService),
                    "Vendor-" + (i + 1)
            ));
        }
    }

    /**
     * Creates Customer threads based on the configuration.
     *
     * @param config     The main configuration object containing the number of customers.
     * @param ticketPool The shared TicketPool resource.
     */
    private void createCustomerThreads(MainConfig config, TicketPool ticketPool) {
        for (int i = 0; i < config.getNumberOfCustomers(); i++) {
            threads.add(new Thread(
                    new Customer(ticketPool, config.getCustomerRetrievalRate(), loggingService),
                    "Customer-" + (i + 1)
            ));
        }
    }

    /**
     * Starts a scheduled task to log the live ticket count at regular intervals.
     *
     * @param ticketPool The shared TicketPool resource.
     */
    private void startLiveCountLogger(TicketPool ticketPool) {
        liveCountLogger = Executors.newSingleThreadScheduledExecutor();
        liveCountLogger.scheduleAtFixedRate(() -> {
            int currentCount = ticketPool.getCurrentTicketCount();
            String logMessage = "Live Ticket Count: " + currentCount;
            loggingService.addLog(logMessage);
        }, 0, LOG_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * Stops the live ticket count logger if it is running.
     */
    private void stopLiveCountLogger() {
        if (liveCountLogger != null) {
            liveCountLogger.shutdownNow();
        }
    }

    /**
     * Logs a message using the LoggingService.
     *
     * @param message The message to be logged.
     */
    private void logMessage(String message) {
        loggingService.addLog(message);
    }
}
