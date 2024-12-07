package com.janindu.ticket.model;

import com.janindu.ticket.service.LoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

@Component
public class TicketPool {
    private static final Logger logger = LoggerFactory.getLogger(TicketPool.class);
    private final Queue<String> tickets = new LinkedList<>();
    private final LoggingService loggingService;

    public TicketPool(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    public synchronized void addTicket(String ticket) {
        tickets.add(ticket);
        logMessage("Added ticket: " + ticket);
        notifyAll();
    }

    public synchronized String removeTicket() throws InterruptedException {
        while (tickets.isEmpty()) {
            wait();
        }
        String ticket = tickets.poll();
        logMessage("Removed ticket: " + ticket);
        return ticket;
    }

    public synchronized int getAvailableTickets() {
        return tickets.size();
    }

    // **Updated Method to Get Current Ticket Count**
    public synchronized int getCurrentTicketCount() {
        return getAvailableTickets();
    }

    private void logMessage(String message) {
        logger.info(message);
        loggingService.addLog(message);
    }
}
