package com.treatsboot.repositories;

import org.springframework.stereotype.Repository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Repository
public class MediaRepository
{
    public byte[] getLatestMedia() throws IOException
    {
        File fl = new File("./media");
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

    public String getFullFilename(String filename)
    {
        return "./media/" + filename;
    }

    public byte[] getMedia(String filename) throws IOException
    {
        BufferedImage image = ImageIO.read(new File(getFullFilename(filename)));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "gif", baos);
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();

        return imageBytes;
    }
}
