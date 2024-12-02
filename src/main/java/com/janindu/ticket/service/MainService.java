package com.janindu.ticket.service;

import com.janindu.ticket.model.MainConfig;
import com.janindu.ticket.model.TicketPool;
import com.janindu.ticket.repository.SystemConfigRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MainService {

    private final SystemConfigRepository configRepository;
    private final ThreadService threadService;

    public MainService(SystemConfigRepository configRepository, ThreadService threadService) {
        this.configRepository = configRepository;
        this.threadService = threadService;
    }

    public MainConfig saveConfig(MainConfig config) {
        clearConfig();
        return configRepository.save(config);
    }

    public Optional<MainConfig> getConfig() {
        return configRepository.findAll().stream().findFirst();
    }

    public void clearConfig() {
        configRepository.deleteAll();
    }

    public void startSystem(Optional<MainConfig> config, TicketPool ticketPool) {
        if (config.isEmpty()) {
            throw new IllegalStateException("No configuration found. Please configure the system first.");
        }
        threadService.configure(config.get(), ticketPool);
        threadService.startThreads();
    }

    public void stopSystem() {
        threadService.stopThreads();
    }
}
