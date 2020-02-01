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

    public void on() throws IOException
    {
        /*
        eventRepository.push("Turning on light.  This takes a few seconds...");

        ProcessBuilder pb = new ProcessBuilder("./lightOn.sh");
        pb.start();
        try
        {
            // this takes forever
            Thread.sleep(5000);
        }
        catch (InterruptedException e)
        {
            // don't care if this fails, probably just get a dark image for a second
        }*/
    }

    @Async
    public void off() throws IOException
    {
        /*
        try
        {
            // delay a bit for fudge factor
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            // don't care if this fails, probably just get a dark image for a second
        }

        eventRepository.push("Turning off light...");
        ProcessBuilder pb = new ProcessBuilder("./lightOff.sh");
        pb.start();
        */
    }
}
