package com.treatsboot.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sound.sampled.*;
import java.io.IOException;

@Component
public class Microphone
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Microphone.class);

    // the line from which audio data is captured
    private TargetDataLine line;

    private static AudioFormat audioFormat = new AudioFormat(
        44100,16,1,true,false);

    public Microphone()
    {
    }

    /**
     * Prepare the line for recording
     *
     * @return
     */
    public void open(LineListener listener)
    {
        try
        {
            line = AudioSystem.getTargetDataLine(audioFormat);
            line.addLineListener(listener);
            line.open(audioFormat);

        } catch (LineUnavailableException ex)
        {
            LOGGER.error(ex.toString(), ex);
        }
    }

    /**
     * Captures the sound and return the stream
     * @throws IOException
     */
    public MicrophoneInputStream start() {

        AudioInputStream audioInputStream = new AudioInputStream(line);
        line.start(); // start capturing
        return new MicrophoneInputStream(audioInputStream);
    }

    /**
     * Stops the recording process
     */
    public void stop() {
        line.stop();
        LOGGER.info("Microphone Off");
    }

    /**
     * Closes the target data line to finish capturing and recording	 *
     */
    public void close() {
        line.stop();
        line.close();
    }
}
