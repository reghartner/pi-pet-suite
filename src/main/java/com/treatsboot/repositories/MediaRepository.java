package com.treatsboot.repositories;

import com.treatsboot.exceptions.GTFOException;
import com.treatsboot.exceptions.MediaNotYetAvailableException;
import org.springframework.stereotype.Repository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

@Repository
public class MediaRepository
{
    private Set<String> filenames;
    private Set<String> futureFilenames = new HashSet<>();

    private final String mediaFolder = "/media";

    private Set<String> LazyFilenames()
    {
        if(filenames == null)
        {
            filenames = new HashSet<>();
            System.out.println("Initializing existing filenames");
            File folder = new File(mediaFolder);
            File[] listOfFiles = folder.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                filenames.add(listOfFiles[i].getName());
                System.out.println(format("adding file to list: %s", listOfFiles[i].getName()));
            }
        }
        return filenames;
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
        return getMedia(latest.getName());
    }

    public byte[] getMedia(String filename) throws IOException
    {
        if (this.LazyFilenames().contains(filename))
        {
            BufferedImage image = ImageIO.read(new File(mediaFolder + filename));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "gif", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            return imageBytes;
        }
        else if (futureFilenames.contains(filename))
        {
            String message = format("%s is not yet available.  Try again soon", filename);
            System.out.println(message);
            throw new MediaNotYetAvailableException(message);
        }

        String message = format("Don't request files that don't exist", filename);
        System.out.println(message);
        throw new GTFOException(message);
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
