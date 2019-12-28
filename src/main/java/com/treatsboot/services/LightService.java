package com.treatsboot.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LightService
{
    @Async
    public void on() throws IOException
    {
        ProcessBuilder pb = new ProcessBuilder("./lightOn.sh");
        Process p = pb.start();
    }

    @Async
    public void off() throws IOException
    {
        ProcessBuilder pb = new ProcessBuilder("./lightOff.sh");
        Process p = pb.start();
    }
}
