package com.treatsboot.utilities;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;

public class PCMSigned8Bit extends AbstractSignedLevelConverter
{

    public PCMSigned8Bit(AudioFormat sourceFormat)
    {
        super(sourceFormat);
    }

    public double convertToLevel(byte[] chunk) throws IOException
    {
        if (chunk.length != getRequiredChunkByteSize())
            return -1;

        AudioInputStream ais = convert(chunk);
        ais.read(chunk, 0, chunk.length);

        return (double) chunk[0];
    }
}