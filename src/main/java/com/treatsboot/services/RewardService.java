package com.treatsboot.services;

import com.treatsboot.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.treatsboot.utilities.DurationHelpers.getPrettyDuration;
import static java.lang.String.format;

@Service
public class RewardService
{
    private final MicrophoneService mic;
    private final TreatDispenserService treats;
    private final CameraService camera;
    private final EventRepository eventRepository;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private boolean kill = false;
    private boolean rewardInProgress = false;

    @Autowired
    public RewardService(
        MicrophoneService mic,
        TreatDispenserService treats,
        CameraService camera,
        EventRepository eventRepository)
    {
        this.mic = mic;
        this.treats = treats;
        this.camera = camera;
        this.eventRepository = eventRepository;
    }

    /**
     * Dispenses treats and records a gif.
     * @param smallTreat
     * @return
     * @throws Exception
     */
    public void dispenseAndRecord(boolean smallTreat) throws Exception
    {
        if (rewardInProgress)
        {
            eventRepository.push("There is already a reward in progress.  Either wait until it completes or kill it before starting a new reward");
            return;
        }

        rewardInProgress = true;
        String filename = getGifName();
        camera.recordAndSaveGif(filename, 100, () ->
        {
            eventRepository.push("%s treat coming!", smallTreat ? "Small" : "Big");
            rewardInProgress = false;
            treats.treat(smallTreat);
            return null;
        });
    }

    /**
     * Dispenses treats after n minutes of silence and records a gif.
     * @param smallTreat
     * @return
     * @throws Exception
     */
    public void rewardForSilence(double minutes, boolean smallTreat) throws Exception
    {
        if (rewardInProgress)
        {
            eventRepository.push("There is already a reward in progress.  Either wait until it completes or kill it before starting a new reward");
            return;
        }
        rewardInProgress = true;
        eventRepository.push(
            "Will reward Harley with a %s treat after he's silent for %s minutes.",
            smallTreat ? "small" : "big",
            String.valueOf(minutes));

        long startTime = new Date().getTime();
        mic.callbackAfterNMinutesOfSilence(minutes, () ->
        {
            eventRepository.push(
                "He made it.  It took him %s until he was silent for %s minutes. %s treat coming!",
                getPrettyDuration(new Date().getTime() - startTime),
                String.valueOf(minutes),
                smallTreat ? "Small" : "Big");
            rewardInProgress = false;
            dispenseAndRecord(smallTreat);
            return null;
        });
    }

    /**
     * Will treat a small amount of treats every *intervalMinutes* for *totalMinutes*
     *
     * Gifs will be recorded
     * @param intervalMinutes
     * @param totalMinutes
     */
    public void rewardForSilenceOverTime(double intervalMinutes, double totalMinutes) throws Exception
    {
        if (rewardInProgress)
        {
            eventRepository.push("There is already a reward in progress.  Either wait until it completes or kill it before starting a new reward");
            return;
        }
        rewardInProgress = true;

        long startTime = System.currentTimeMillis();
        long endTime = startTime + (int)(totalMinutes * 60000);
        int treatsDispensed = 0;

        kill = false;
        while(System.currentTimeMillis() < endTime && !kill)
        {
            rewardForSilence(intervalMinutes, true);
            treatsDispensed++;
        }

        if (kill)
        {
            eventRepository.push("Rolling rewards killed");
            kill = false;
        }
        else
        {
            eventRepository.push(format("Times up.  Total treats dispensed: %s", treatsDispensed));
        }

        rewardInProgress = false;
    }

    /**
     * Kill any existing reward timers and mic listeners
     */
    public void kill()
    {
        this.kill = true;
        this.rewardInProgress = false;
        mic.kill();
    }

    private String getGifName() { return format("%s.gif", sdf.format(new Date())); }
}
