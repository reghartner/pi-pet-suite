package com.treatsboot.repositories;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Repository
public class EventRepository
{
    private List<String> events = new ArrayList<>();
    private Lock lock = new ReentrantLock();

    public void push(String event)
    {
        System.out.println(event);
        lock.lock();
        events.add(Date.from(Instant.now()) + ": " + event);
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
