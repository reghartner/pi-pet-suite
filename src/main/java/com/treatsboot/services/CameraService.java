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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

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

    public byte[] getGif(int seconds) throws Exception
    {
        int msBetweenFrames = 100;
        int numFrames = seconds * (1000 / msBetweenFrames);

        CameraConfiguration config = cameraConfiguration()
            .width(1024)
            .height(768)
            .encoding(Encoding.JPEG)
            .quality(90)
            .rotation(90);

        ByteArrayPictureCaptureHandler handler = new ByteArrayPictureCaptureHandler();

        try(Camera camera = new Camera(config))
        {
            // let the camera warm up
            Thread.sleep(2000);

            ByteArrayOutputStream gifByteStream = new ByteArrayOutputStream();
            ImageOutputStream output = ImageIO.createImageOutputStream(gifByteStream);

            // create a gif sequence with the type of the first image, 1 second
            // between frames, which loops continuously
            GifSequenceWriter writer = new GifSequenceWriter(output, 1, msBetweenFrames, true);

            for (int i = 0; i < 1; i++)
            {
                camera.takePicture(handler);
                System.out.println("handler result: " + handler.result());
                InputStream in = new ByteArrayInputStream(handler.result());
                BufferedImage bImageFromConvert = ImageIO.read(in);

                System.out.println("bimage: " + bImageFromConvert.toString());
                writer.writeToSequence(bImageFromConvert);
            }

            byte[] gifBytes = gifByteStream.toByteArray();
            System.out.println("gifBytes: " + gifBytes);

            writer.close();
            gifByteStream.close();
            output.close();

            return gifBytes;
        }
    }
}
