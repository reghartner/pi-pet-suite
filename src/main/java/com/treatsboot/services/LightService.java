package com.treatsboot.services;

import com.treatsboot.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LightService
{

    private EventRepository eventRepository;

    @Autowired
    public LightService(EventRepository eventRepository)
    {
        this.eventRepository = eventRepository;
    }

    @Async
    public void on() throws IOException
    {
        eventRepository.push("Turning on light...");
        ProcessBuilder pb = new ProcessBuilder("./lightOn.sh");
        pb.start();
    }

    @Async
    public void off() throws IOException
    {
        eventRepository.push("Turning off light...");
        ProcessBuilder pb = new ProcessBuilder("./lightOff.sh");
        pb.start();
    }
}
