package com.treatsboot.services;

import com.treatsboot.repositories.EventRepository;
import com.treatsboot.utilities.Microphone;
import com.treatsboot.utilities.MicrophoneInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Callable;

import static com.treatsboot.utilities.DurationHelpers.getPrettyDuration;

@Service
public class MicrophoneService implements LineListener {

    private static int NOISE_THRESHOLD = 4500;
    private Microphone microphone;
    private EventRepository eventRepository;

    private long startTime;
    private long endTime;

    @Autowired
    public MicrophoneService(Microphone microphone, EventRepository eventRepository)
    {
        this.microphone = microphone;
        this.eventRepository = eventRepository;
    }

    /**
     * Reset the timers.  If the sleep flag is true, sleeps for 1000ms so we don't trigger the
     * mic multiple times in the same event
     * @param ms
     * @param sleep
     */
    private void resetTimer(int ms, boolean sleep) throws InterruptedException
    {
        startTime = new Date().getTime();
        endTime = startTime + ms;

        if(sleep)
        {
            Thread.sleep(1000);
        }

    }

    private boolean kill = false;
    public void kill()
    {
        kill = true;
    }

    public void callbackAfterNMinutesOfSilence(double minutes, Callable callback) throws Exception
    {
        int milliseconds = (int)(minutes * 60 * 1000);
        resetTimer(milliseconds, false);

        microphone.open(this);
        MicrophoneInputStream inputStream = microphone.start();
        kill = false;
        while(!kill)
        {
            if (new Date().getTime() > endTime)
            {
                microphone.stop();
                microphone.close();
                callback.call();
                return;
            }
            else
            {
                try
                {
                    byte[] chunk = inputStream.readNBytes(1024);
                    if (inputStream.calculateRMSLevel(chunk) > NOISE_THRESHOLD)
                    {
                        long now = new Date().getTime();
                        long silentMs = now - startTime;
                        eventRepository.push(
                            "Mic triggered after " + getPrettyDuration(silentMs) + " of silence.  Resetting timer.");
                        resetTimer(milliseconds, true);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        if (kill)
        {
            eventRepository.push("Mic timer killed");
            microphone.stop();
            microphone.close();
        }
        kill = false;
    }

    @Override
    public void update(LineEvent event)
    {
        // no-op
    }
}
