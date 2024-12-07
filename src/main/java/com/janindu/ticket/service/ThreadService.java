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

@Service
public class ThreadService {

    private final LoggingService loggingService;
    private List<Thread> threads = new ArrayList<>();
    private volatile boolean running = false;
    private ScheduledExecutorService liveCountLogger;
    private final int LOG_INTERVAL = 1; // Log interval in seconds

    public ThreadService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    public void configure(MainConfig config, TicketPool ticketPool) {
        threads.clear();
        createVendorThreads(config, ticketPool);
        createCustomerThreads(config, ticketPool);

        logMessage("System configured with " + config.getNumberOfVendors() + " vendors and " + config.getNumberOfCustomers() + " customers.");
    }

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

    public void stopThreads() {
        if (!running) {
            throw new IllegalStateException("Threads are not running.");
        }
        threads.forEach(Thread::interrupt);
        stopLiveCountLogger();
        running = false;

        logMessage("All threads stopped.");
    }

    private void createVendorThreads(MainConfig config, TicketPool ticketPool) {
        for (int i = 0; i < config.getNumberOfVendors(); i++) {
            threads.add(new Thread(
                    new Vendor(ticketPool, config.getTicketReleaseRate(), config.getMaxTicketCapacity(), loggingService),
                    "Vendor-" + (i + 1)
            ));
        }
    }

    private void createCustomerThreads(MainConfig config, TicketPool ticketPool) {
        for (int i = 0; i < config.getNumberOfCustomers(); i++) {
            threads.add(new Thread(
                    new Customer(ticketPool, config.getCustomerRetrievalRate(), loggingService),
                    "Customer-" + (i + 1)
            ));
        }
    }

    private void startLiveCountLogger(TicketPool ticketPool) {
        liveCountLogger = Executors.newSingleThreadScheduledExecutor();
        liveCountLogger.scheduleAtFixedRate(() -> {
            int currentCount = ticketPool.getCurrentTicketCount();
            String logMessage = "Live Ticket Count: " + currentCount;
            loggingService.addLog(logMessage);
        }, 0, LOG_INTERVAL, TimeUnit.SECONDS);
    }

    private void stopLiveCountLogger() {
        if (liveCountLogger != null) {
            liveCountLogger.shutdownNow();
        }
    }

    private void logMessage(String message) {
        loggingService.addLog(message);
    }
}
