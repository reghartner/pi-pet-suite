package com.treatsboot.utilities;

import javax.sound.sampled.AudioInputStream;
import java.io.FilterInputStream;
import java.io.IOException;

/**
 * Helper class that allows to handle the automatic truncation of the audio stream when using {@link AudioInputStream} if the RMS of the signal goes below some threshold for some time.
 * It is also useful because {@link AudioInputStream} doesn't put the stream to -1 when the registration is stopped, in order to be read externally and reach EOF at some point
 *
 * @author adionisi
 *
 */
public class MicrophoneInputStream extends FilterInputStream
{

    protected MicrophoneInputStream(AudioInputStream in)
    {
        super(in);
    }

    @Override
    public int read() throws IOException {
        int read = super.read();
        if (read == 0) {
            return -1;
        } else {
            return read;
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        int read = super.read(b);
        if (read == 0) {
            return -1;
        } else {
            return read;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = super.read(b, off, len);
        if (read == 0) {
            return -1;
        } else {
            return read;
        }
    }

    /** Computes the RMS volume of a group of signal sizes ranging from -1 to 1. */
    public int calculateRMSLevel(byte[] audioData) {
        // audioData might be buffered data read from a data line
        long lSum = 0;
        for (int i = 0; i < audioData.length; i++) {
            lSum = lSum + audioData[i];
        }

        double dAvg = lSum / audioData.length;

        double sumMeanSquare = 0d;
        for (int j = 0; j < audioData.length; j++) {
            sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 4d);
        }

        double averageMeanSquare = sumMeanSquare / audioData.length;
        return (int) (Math.pow(averageMeanSquare, 0.5d) + 0.5);
    }
}