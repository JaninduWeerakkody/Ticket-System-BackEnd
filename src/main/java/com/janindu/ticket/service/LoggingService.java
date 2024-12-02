package com.janindu.ticket.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LoggingService {
    private final List<String> logs = Collections.synchronizedList(new ArrayList<>());

    public synchronized void addLog(String log) {
        logs.add(log);
    }

    public synchronized List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    public synchronized void clearLogs() {
        logs.clear();
    }
}
