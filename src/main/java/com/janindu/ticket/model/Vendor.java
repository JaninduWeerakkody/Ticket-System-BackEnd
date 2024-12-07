package com.janindu.ticket.model;

import com.janindu.ticket.service.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vendor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Vendor.class);
    private final TicketPool ticketPool;
    private final int ticketsToRelease;
    private final int maxCapacity;
    private final LoggingService loggingService;

    public Vendor(TicketPool ticketPool, int ticketsToRelease, int maxCapacity, LoggingService loggingService) {
        this.ticketPool = ticketPool;
        this.ticketsToRelease = ticketsToRelease;
        this.maxCapacity = maxCapacity;
        this.loggingService = loggingService;
    }

    @Override
    public void run() {
        for (int i = 0; i < ticketsToRelease; i++) {
            if (releaseTicket()) break;
            delay(500); // Adjusted delay for slower ticket release
        }
    }

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

    private void delay(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logMessage("Vendor interrupted");
        }
    }

    private void logMessage(String message) {
        String fullMessage = Thread.currentThread().getName() + ": " + message;
        logger.info(fullMessage);
        loggingService.addLog(fullMessage);
    }
}
