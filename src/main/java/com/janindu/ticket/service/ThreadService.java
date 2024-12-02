package com.janindu.ticket.service;

import com.janindu.ticket.model.Customer;
import com.janindu.ticket.model.MainConfig;
import com.janindu.ticket.model.TicketPool;
import com.janindu.ticket.model.Vendor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ThreadService {

    private final LoggingService loggingService;
    private List<Thread> threads = new ArrayList<>();
    private volatile boolean running = false;

    public ThreadService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    public void configure(MainConfig config, TicketPool ticketPool) {
        threads.clear();
        createVendorThreads(config, ticketPool);
        createCustomerThreads(config, ticketPool);

        logMessage("System configured with " + config.getNumberOfVendors() + " vendors and " + config.getNumberOfCustomers() + " customers.");
    }

    public void startThreads() {
        if (running) {
            throw new IllegalStateException("Threads are already running.");
        }
        running = true;
        threads.forEach(Thread::start);

        logMessage("All threads started.");
    }

    public void stopThreads() {
        if (!running) {
            throw new IllegalStateException("Threads are not running.");
        }
        threads.forEach(Thread::interrupt);
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

    private void logMessage(String message) {
        loggingService.addLog(message);
    }
}
