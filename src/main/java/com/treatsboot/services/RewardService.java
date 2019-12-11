package com.treatsboot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.treatsboot.utilities.DurationHelpers.getPrettyDuration;
import static java.lang.String.format;

@Service
public class RewardService
{

    private final MicrophoneService mic;
    private final TreatDispenserService treats;
    private final CameraService camera;

    @Autowired
    public RewardService(MicrophoneService mic, TreatDispenserService treats, CameraService camera)
    {
        this.mic = mic;
        this.treats = treats;
        this.camera = camera;
    }

    /**
     * Will treat treats once after n minutes of silence
     * @param minutes
     */
    public void rewardForSilence(double minutes, boolean smallTreat) throws InterruptedException
    {
        System.out.println(format(
            "Will reward Harley with a % treat after he's silent for %s minutes.",
            minutes,
            smallTreat ? "small" : "big"));

        long startTime = new Date().getTime();
        mic.returnAfterMinutesOfSilence(minutes);
        long endTime = new Date().getTime();

        System.out.println(format(
            "He made it.  It took him %s until he was silent for %s minutes. % treat coming!",
            getPrettyDuration(endTime-startTime),
            minutes,
            smallTreat ? "Small" : "Big"));

        if (smallTreat)
        {
            treats.smallTreat();
        }
        else
        {
            treats.treat();
        }
    }

    /**
     * Starts Dispensing Treats, takes a picture while they are coming out and returns the picture
     */
    public byte[] dispenseAndSnap() throws Exception
    {
        treats.treat();
        Thread.sleep(500);
        return camera.snap();
    }

    /**
     * Will treat a small amount of treats every *intervalMinutes* for *totalMinutes*
     * @param intervalMinutes
     * @param totalMinutes
     */
    public void rewardForSilenceOverTime(double intervalMinutes, double totalMinutes)
        throws InterruptedException
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
