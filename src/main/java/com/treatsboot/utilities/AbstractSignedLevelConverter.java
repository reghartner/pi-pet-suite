package com.treatsboot.utilities;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.IOException;

abstract class AbstractSignedLevelConverter
{
    private AudioFormat srcf;

    public AbstractSignedLevelConverter(AudioFormat sourceFormat)
    {
        srcf = sourceFormat;
    }

    public AudioInputStream convert(byte[] chunk)
    {
        AudioInputStream ais = null;
        if(AudioSystem.isConversionSupported(   AudioFormat.Encoding.PCM_SIGNED,
            srcf))
        {
            if(srcf.getEncoding() != AudioFormat.Encoding.PCM_SIGNED)
                ais = AudioSystem.getAudioInputStream(
                    AudioFormat.Encoding.PCM_SIGNED,
                    new AudioInputStream(new ByteArrayInputStream(chunk),
                        srcf,
                        chunk.length * srcf.getFrameSize()));
            else
                ais = new AudioInputStream(new ByteArrayInputStream(chunk),
                    srcf,
                    chunk.length * srcf.getFrameSize());
        }

        return ais;
    }

    abstract public double convertToLevel(byte[] chunk)  throws IOException;

    public int getRequiredChunkByteSize()
    {
        return srcf.getFrameSize();
    }
}