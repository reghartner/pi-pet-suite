package com.treatsboot.repositories;

import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.treatsboot.exceptions.GTFOException;
import com.treatsboot.exceptions.MediaNotYetAvailableException;
import org.springframework.stereotype.Repository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;

@Repository
public class MediaRepository
{
    private Set<String> filenames;
    private Set<String> futureFilenames = new HashSet<>();

    private final String mediaFolder = "media/";
    private final String indexFile = mediaFolder + "filenames.txt";

    public MediaRepository() throws IOException
    {
        File indexFile = new File(this.indexFile);
        indexFile.createNewFile(); // if file already exists will do nothing

        filenames = Sets.newHashSet(Files.readLines(indexFile, Charset.defaultCharset()));
    }

    public byte[] getMedia(String filename) throws IOException
    {
        if(filenames.contains(filename))
        {
            BufferedImage image = ImageIO.read(new File(filename));
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

    public void registerFilename(String filename)
    {
        this.futureFilenames.add(filename);
    }

    public void fileWritten(String filename) throws IOException
    {
        this.futureFilenames.remove(filename);
        this.filenames.add(filename);
        Writer output = new BufferedWriter(new FileWriter(indexFile, true));
        output.append(filename);
        output.close();
    }
}
