package com.treatsboot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

import static com.treatsboot.utilities.DurationHelpers.getPrettyDuration;
import static java.lang.String.format;

@Service
public class RewardService
{

    private final MicrophoneService mic;
    private final TreatDispenserService treats;
    private final CameraService camera;

    private long startTime;
    private boolean smallTreat;
    private double minutes;

    @Autowired
    public RewardService(MicrophoneService mic, TreatDispenserService treats, CameraService camera)
    {
        this.mic = mic;
        this.treats = treats;
        this.camera = camera;
    }

    /**
     * Dispenses treats after n minutes of silence and records a gif.  Returns the name of the file for retrieval.
     * @param smallTreat
     * @return
     * @throws Exception
     */
    public String rewardForSilence(double minutes, boolean smallTreat)
        throws Exception
    {
        this.minutes = minutes;
        this.smallTreat = smallTreat;
        this.startTime = new Date().getTime();

        String filename = getGifName();
        System.out.println(format(
            "Will reward Harley with a %s treat after he's silent for %s minutes.",
            minutes,
            smallTreat ? "small" : "big"));

        mic.callbackAfterNMinutesOfSilence(minutes, this::silenceCallback);

        return filename;
    }

    /**
     * return object due to constraints of using Callable
     * @return
     */
    private Object silenceCallback()
    {
        long endTime = new Date().getTime();

        System.out.println(format(
            "He made it.  It took him %s until he was silent for %s minutes. % treat coming!",
            getPrettyDuration(endTime-startTime),
            minutes,
            smallTreat ? "Small" : "Big"));

        treats.treat(smallTreat);
        return null;
    }

    /**
     * Dispenses treats and records a gif.  Returns the name of the file for retrieval.
     * @param smallTreat
     * @return
     * @throws Exception
     */
    public String dispenseAndRecord(boolean smallTreat) throws Exception
    {
        String filename = getGifName();
        treats.treat(smallTreat);
        camera.recordAndSaveGif(filename);
        return filename;
    }

    /**
     * Kill any existing mic listeners
     */
    public void kill()
    {
        mic.kill();
    }

    private String getGifName()
    {
        return format("%s.gif", Date.from(Instant.now()));
    }

    /**
     * Will treat a small amount of treats every *intervalMinutes* for *totalMinutes*
     *
     * Gifs will be recorded, but the names of the files are not returned.
     * @param intervalMinutes
     * @param totalMinutes
     */
    public void rewardForSilenceOverTime(double intervalMinutes, double totalMinutes)
        throws Exception
    {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (int)(totalMinutes * 60000);
        int treatsDispensed = 0;

        while(System.currentTimeMillis() < endTime)
        {
            rewardForSilence(intervalMinutes, true);
            treatsDispensed++;
        }

        System.out.println(format("Times up.  Total treats dispensed: " + treatsDispensed));
    }
}
