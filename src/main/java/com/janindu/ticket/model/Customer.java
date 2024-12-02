package com.janindu.ticket.model;

import jakarta.persistence.*;

import com.janindu.ticket.service.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Customer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Customer.class);
    private final TicketPool ticketPool;
    private final int retrievalLimit;
    private final LoggingService loggingService;

    public Customer(TicketPool ticketPool, int retrievalLimit, LoggingService loggingService) {
        this.ticketPool = ticketPool;
        this.retrievalLimit = retrievalLimit;
        this.loggingService = loggingService;
    }

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
                        String logMessage = Thread.currentThread().getName() + ": Purchased " + ticket;
                        logger.info(logMessage);
                        loggingService.addLog(logMessage);
                    }
                }
                Thread.sleep(300); // Simulate delay
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
