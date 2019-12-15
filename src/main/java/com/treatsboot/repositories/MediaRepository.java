package com.treatsboot.repositories;

import com.treatsboot.exceptions.GTFOException;
import com.treatsboot.exceptions.MediaNotYetAvailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

@Repository
public class MediaRepository
{
    private Set<String> filenames = new HashSet<>();
    private Set<String> futureFilenames = new HashSet<>();

    private final String mediaFolder = "/media";

    @Autowired
    public MediaRepository()
    {
        System.out.println("Initializing existing filenames");
        File folder = new File(mediaFolder);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            filenames.add(listOfFiles[i].getName());
        }
    }

    public byte[] getLatestMedia() throws IOException
    {
        File fl = new File(mediaFolder);
        File[] files = fl.listFiles(file -> file.isFile());
        long lastMod = Long.MIN_VALUE;
        File latest = null;
        for (File file : files) {
            if (file.lastModified() > lastMod) {
                latest = file;
                lastMod = file.lastModified();
            }
        }
        return getMedia(latest.getAbsolutePath());
    }

    public byte[] getMedia(String filename) throws IOException
    {
        if(filenames.contains(filename))
        {
            BufferedImage image = ImageIO.read(new File(mediaFolder + filename));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( image, "gif", baos );
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            return imageBytes;
        }
        else if(futureFilenames.contains(filename))
        {
            throw new MediaNotYetAvailableException(format("%s is not yet available.  Try again soon", filename));
        }

        throw new GTFOException("Do not request files that don't exist!");
    }

    public String registerFilename(String filename)
    {
        this.futureFilenames.add(filename);
        return mediaFolder + filename;
    }

    public void fileWritten(String filename) throws IOException
    {
        this.futureFilenames.remove(filename);
        this.filenames.add(filename);
    }
}
