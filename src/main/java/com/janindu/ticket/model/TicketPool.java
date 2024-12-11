package com.janindu.ticket.model;

import com.janindu.ticket.service.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * TicketPool manages the pool of tickets in the system.
 * It allows adding, removing, and retrieving the count of tickets.
 */
@Component
public class TicketPool {

    private static final Logger logger = LoggerFactory.getLogger(TicketPool.class);

    // List to store tickets.
    private final List<String> tickets = new ArrayList<>();

    // Service to log system messages.
    private final LoggingService loggingService;

    /**
     * Constructor for TicketPool.
     *
     * @param loggingService The service used for logging messages.
     */
    public TicketPool(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    /**
     * Adds a ticket to the pool and notifies any waiting threads.
     *
     * @param ticket The ticket to be added.
     */
    public synchronized void addTicket(String ticket) {
        tickets.add(ticket);
        logMessage("Added ticket: " + ticket);
        notifyAll(); // Notify threads waiting for tickets.
    }

    /**
     * Removes a ticket from the pool. Waits if no tickets are available.
     *
     * @return The removed ticket.
     * @throws InterruptedException If the thread is interrupted while waiting.
     */
    public synchronized String removeTicket() throws InterruptedException {
        while (tickets.isEmpty()) {
            wait(); // Wait until a ticket is available.
        }
        String ticket = tickets.remove(0); // Remove the first ticket.
        logMessage("Removed ticket: " + ticket);
        return ticket;
    }

    /**
     * Gets the number of available tickets in the pool.
     *
     * @return The count of available tickets.
     */
    public synchronized int getAvailableTickets() {
        return tickets.size();
    }

    /**
     * Gets the current ticket count in the pool.
     *
     * @return The current ticket count.
     */
    public synchronized int getCurrentTicketCount() {
        return tickets.size();
    }

    /**
     * Logs a message and adds it to the logging service.
     *
     * @param message The message to log.
     */
    private void logMessage(String message) {
        logger.info(message);
        loggingService.addLog(message);
    }
}