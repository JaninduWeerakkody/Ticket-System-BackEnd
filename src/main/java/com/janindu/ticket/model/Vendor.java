package com.janindu.ticket.model;

import com.janindu.ticket.service.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vendor class represents a producer that adds tickets to the TicketPool.
 * Implements Runnable to support multithreading.
 */
public class Vendor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Vendor.class);
    private final TicketPool ticketPool;
    private final int ticketsToRelease;
    private final int maxCapacity;
    private final LoggingService loggingService;

    /**
     * Constructor to initialize Vendor with required parameters.
     *
     * @param ticketPool      The shared TicketPool resource.
     * @param ticketsToRelease Number of tickets to release.
     * @param maxCapacity      Maximum capacity of the TicketPool.
     * @param loggingService   Service for logging operations.
     */
    public Vendor(TicketPool ticketPool, int ticketsToRelease, int maxCapacity, LoggingService loggingService) {
        this.ticketPool = ticketPool;
        this.ticketsToRelease = ticketsToRelease;
        this.maxCapacity = maxCapacity;
        this.loggingService = loggingService;
    }

    /**
     * The run method for the Vendor thread. It releases tickets into the TicketPool
     * until the specified number of tickets is released or the pool reaches its maximum capacity.
     */
    @Override
    public void run() {
        for (int i = 0; i < ticketsToRelease; i++) {
            if (releaseTicket()) break; // Stop releasing if max capacity is reached
            delay(500); // Delay between ticket releases (in milliseconds)
        }
    }

    /**
     * Releases a ticket to the TicketPool. If the pool reaches max capacity, stops releasing.
     *
     * @return true if the max capacity is reached, false otherwise.
     */
    private boolean releaseTicket() {
        synchronized (ticketPool) {
            if (ticketPool.getAvailableTickets() >= maxCapacity) {
                logMessage("Max ticket capacity reached. Pausing ticket release.");
                return true;
            }
            String ticket = "Ticket-" + System.nanoTime();
            ticketPool.addTicket(ticket);
            logMessage("Added " + ticket + ". Current ticket count: " + ticketPool.getCurrentTicketCount());
        }
        return false;
    }

    /**
     * Introduces a delay to simulate time taken for ticket release.
     *
     * @param duration Duration of the delay in milliseconds.
     */
    private void delay(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logMessage("Vendor interrupted");
        }
    }

    /**
     * Logs a message with the thread name for context.
     *
     * @param message The message to log.
     */
    private void logMessage(String message) {
        String fullMessage = Thread.currentThread().getName() + ": " + message;
        logger.info(fullMessage);
        loggingService.addLog(fullMessage);
    }
}

