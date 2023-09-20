package com.catalog.utils.rabbit;

@FunctionalInterface
public interface EventProcessor {
    void process(RabbitEvent event);
}