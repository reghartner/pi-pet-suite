package com.treatsboot.repositories;

import org.springframework.stereotype.Repository;

import java.io.*;

@Repository
public class MediaRepository
{
    public byte[] getLatestMedia() throws IOException
    {
        File fl = new File("./media");
        File[] files = fl.listFiles(file -> file.isFile() && file.length() > 0);
        long lastMod = Long.MIN_VALUE;
        File latest = null;
        for (File file : files) {
            if (file.lastModified() > lastMod) {
                latest = file;
                lastMod = file.lastModified();
            }
        }

        return latest == null ? new byte[0] : getMedia(latest.getName());
    }

    public String getFullFilename(String filename)
    {
        return "./media/" + filename;
    }

    public byte[] getMedia(String filename) throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream in = new FileInputStream(new File(getFullFilename(filename)));

        try
        {
            byte[] buffer = new byte[1024];
            int count;

            while ((count = in.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, count);
            }

            // Flush out stream, to write any remaining buffered data
            outputStream.flush();
        }
        finally
        {
            in.close();
        }

        byte[] imageBytes = outputStream.toByteArray();
        outputStream.flush();
        outputStream.close();
        return imageBytes;
    }
}
