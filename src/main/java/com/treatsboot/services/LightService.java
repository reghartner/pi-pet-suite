package com.treatsboot.services;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LightService
{
    public void on() throws IOException
    {
        ProcessBuilder pb = new ProcessBuilder("./lightOn.sh");
        Process p = pb.start();
    }

    public void off() throws IOException
    {
        ProcessBuilder pb = new ProcessBuilder("./lightOff.sh");
        Process p = pb.start();
    }
}
