package com.janindu.ticket.model;

import com.janindu.ticket.service.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Customer class represents a consumer that retrieves tickets from the TicketPool.
 * Implements Runnable to support multithreading.
 */
public class Customer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Customer.class);
    private final TicketPool ticketPool; // Shared TicketPool resource
    private final int retrievalLimit; // Number of tickets to retrieve
    private final LoggingService loggingService; // Service to handle logging

    /**
     * Constructor to initialize Customer with required parameters.
     *
     * @param ticketPool     The shared TicketPool resource.
     * @param retrievalLimit Number of tickets to retrieve.
     * @param loggingService Service for logging operations.
     */
    public Customer(TicketPool ticketPool, int retrievalLimit, LoggingService loggingService) {
        this.ticketPool = ticketPool;
        this.retrievalLimit = retrievalLimit;
        this.loggingService = loggingService;
    }

    /**
     * The run method for the Customer thread. It retrieves tickets from the TicketPool
     * until the specified number of tickets is retrieved or the thread is interrupted.
     */
    @Override
    public void run() {
        for (int i = 0; i < retrievalLimit; i++) {
            try {
                synchronized (ticketPool) {
                    if (ticketPool.getAvailableTickets() == 0) {
                        String logMessage = Thread.currentThread().getName() + ": No tickets available. Waiting...";
                        logger.info(logMessage);
                        loggingService.addLog(logMessage);
                    } else {
                        String ticket = ticketPool.removeTicket();
                        String logMessage = Thread.currentThread().getName() + ": Purchased " + ticket +
                                ". Current ticket count: " + ticketPool.getCurrentTicketCount();
                        logger.info(logMessage);
                        loggingService.addLog(logMessage);
                    }
                }
                Thread.sleep(700); // Delay between ticket retrievals (in milliseconds)
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                String logMessage = Thread.currentThread().getName() + ": Customer interrupted";
                logger.error(logMessage, e);
                loggingService.addLog(logMessage);
                break;
            }
        }
        String logMessage = Thread.currentThread().getName() + ": Finished purchasing tickets.";
        logger.info(logMessage);
        loggingService.addLog(logMessage);
    }
}
