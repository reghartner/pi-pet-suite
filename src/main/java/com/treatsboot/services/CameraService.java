package com.treatsboot.services;

import com.treatsboot.utilities.GifSequenceWriter;
import org.springframework.stereotype.Service;
import uk.co.caprica.picam.ByteArrayPictureCaptureHandler;
import uk.co.caprica.picam.Camera;
import uk.co.caprica.picam.CameraConfiguration;
import uk.co.caprica.picam.enums.Encoding;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;

import static uk.co.caprica.picam.CameraConfiguration.cameraConfiguration;

@Service
public class CameraService
{
    public byte[] snap() throws Exception
    {
        CameraConfiguration config = cameraConfiguration()
            .width(1024)
            .height(768)
            .encoding(Encoding.JPEG)
            .quality(90)
            .delay(2000)
            .rotation(90);

        ByteArrayPictureCaptureHandler handler = new ByteArrayPictureCaptureHandler();

        try(Camera camera = new Camera(config))
        {
            camera.takePicture(handler);
            return handler.result();
        }
    }

    public byte[] getGif() throws Exception
    {
        int msBetweenFrames = 500;
        int numFrames = 50;

        CameraConfiguration config = cameraConfiguration()
            .width(600)
            .height(400)
            .encoding(Encoding.JPEG)
            .quality(50)
            .rotation(90);

        ByteArrayPictureCaptureHandler handler = new ByteArrayPictureCaptureHandler();

        try(Camera camera = new Camera(config))
        {
            // let the camera warm up
            Thread.sleep(2000);

            ByteArrayOutputStream gifByteStream = new ByteArrayOutputStream();
            ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(gifByteStream);

            // create a gif sequence with the type of the first image, 1 second
            // between frames, which loops continuously
            GifSequenceWriter writer = new GifSequenceWriter(imageOutputStream, 5, msBetweenFrames, true);

            for (int i = 0; i < numFrames; i++)
            {
                camera.takePicture(handler);
                InputStream in = new ByteArrayInputStream(handler.result());
                BufferedImage bImageFromConvert = ImageIO.read(in);
                writer.writeToSequence(bImageFromConvert);
            }

            imageOutputStream.seek(0);
            while (true) {
                try {
                    gifByteStream.write(imageOutputStream.readByte());
                } catch (EOFException e) {
                    System.out.println("End of Image Stream");
                    break;
                } catch (IOException e) {
                    System.out.println("Error processing the Image Stream");
                    break;
                }
            }
            byte[] gifBytes = gifByteStream.toByteArray();

            writer.close();
            gifByteStream.close();
            imageOutputStream.close();

            return gifBytes;
        }
    }
}
