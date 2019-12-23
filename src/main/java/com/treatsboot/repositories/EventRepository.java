package com.treatsboot.repositories;

import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.String.format;

@Repository
public class EventRepository
{
    private List<String> events = new ArrayList<>();
    private Lock lock = new ReentrantLock();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void push(String message, String... args)
    {
        System.out.println(format(message, args));
        String eventMessage = format(message, args);
        lock.lock();
        events.add(format("%s: %s", sdf.format(new Date()), eventMessage));
        lock.unlock();
    }

    public List<String> getNewEvents()
    {
        lock.lock();
        List<String> toReturn = new ArrayList<>(events);
        events.clear();
        lock.unlock();
        return toReturn;
    }
}
