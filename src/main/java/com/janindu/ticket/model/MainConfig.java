package com.janindu.ticket.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
public class MainConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Total tickets cannot be null")
    @Min(value = 1, message = "Total tickets must be at least 1")
    private Integer totalTickets;

    @NotNull(message = "Ticket release rate cannot be null")
    @Min(value = 1, message = "Ticket release rate must be at least 1")
    private Integer ticketReleaseRate;

    @NotNull(message = "Customer retrieval rate cannot be null")
    @Min(value = 1, message = "Customer retrieval rate must be at least 1")
    private Integer customerRetrievalRate;

    @NotNull(message = "Max ticket capacity cannot be null")
    @Min(value = 1, message = "Max ticket capacity must be at least 1")
    private Integer maxTicketCapacity;

    @NotNull(message = "Number of vendors cannot be null")
    @Min(value = 1, message = "At least 1 vendor is required")
    private Integer numberOfVendors;

    @NotNull(message = "Number of customers cannot be null")
    @Min(value = 1, message = "At least 1 customer is required")
    private Integer numberOfCustomers;

    private boolean active;

    public MainConfig() {
        // Default Constructor
    }

    public MainConfig(Integer totalTickets, Integer ticketReleaseRate, Integer customerRetrievalRate,
                      Integer maxTicketCapacity, Integer numberOfVendors, Integer numberOfCustomers, boolean active) {
        this.totalTickets = totalTickets;
        this.ticketReleaseRate = ticketReleaseRate;
        this.customerRetrievalRate = customerRetrievalRate;
        this.maxTicketCapacity = maxTicketCapacity;
        this.numberOfVendors = numberOfVendors;
        this.numberOfCustomers = numberOfCustomers;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(Integer totalTickets) {
        this.totalTickets = totalTickets;
    }

    public Integer getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public void setTicketReleaseRate(Integer ticketReleaseRate) {
        this.ticketReleaseRate = ticketReleaseRate;
    }

    public Integer getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public void setCustomerRetrievalRate(Integer customerRetrievalRate) {
        this.customerRetrievalRate = customerRetrievalRate;
    }

    public Integer getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    public void setMaxTicketCapacity(Integer maxTicketCapacity) {
        this.maxTicketCapacity = maxTicketCapacity;
    }

    public Integer getNumberOfVendors() {
        return numberOfVendors;
    }

    public void setNumberOfVendors(Integer numberOfVendors) {
        this.numberOfVendors = numberOfVendors;
    }

    public Integer getNumberOfCustomers() {
        return numberOfCustomers;
    }

    public void setNumberOfCustomers(Integer numberOfCustomers) {
        this.numberOfCustomers = numberOfCustomers;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "MainConfig{" +
                "id=" + id +
                ", totalTickets=" + totalTickets +
                ", ticketReleaseRate=" + ticketReleaseRate +
                ", customerRetrievalRate=" + customerRetrievalRate +
                ", maxTicketCapacity=" + maxTicketCapacity +
                ", numberOfVendors=" + numberOfVendors +
                ", numberOfCustomers=" + numberOfCustomers +
                ", active=" + active +
                '}';
    }
}
