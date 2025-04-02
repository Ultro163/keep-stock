package com.example.keepstock.service.handler;

import com.example.keepstock.dto.event.EventSource;

public interface EventHandler<T extends EventSource> {

    boolean canHandle(EventSource eventSource);

    String handleEvent(T eventSource);
}