package com.treatsboot.services;

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

    private static int NOISE_THRESHOLD = 4000;
    private Microphone microphone;

    private long startTime;
    private long endTime;

    @Autowired
    public MicrophoneService(Microphone microphone)
    {
        this.microphone = microphone;
    }

    /**
     * Reset the timers.  If the sleep flag is true, sleeps for 500ms so we don't trigger the
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
            Thread.sleep(500);
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
        while(!kill)
        {
            if (new Date().getTime() > endTime)
            {
                microphone.stop();
                microphone.close();
                callback.call();
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
                        System.out.println("Triggered after " + getPrettyDuration(silentMs) + " of silence.  Resetting timer.");
                        resetTimer(milliseconds, true);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        kill = false;
    }

    @Override
    public void update(LineEvent event)
    {
        // no-op
    }
}
