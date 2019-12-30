package com.treatsboot.services;

import com.treatsboot.repositories.EventRepository;
import com.treatsboot.repositories.MediaRepository;
import com.treatsboot.utilities.GifSequenceWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.co.caprica.picam.ByteArrayPictureCaptureHandler;
import uk.co.caprica.picam.Camera;
import uk.co.caprica.picam.CameraConfiguration;
import uk.co.caprica.picam.enums.Encoding;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static uk.co.caprica.picam.CameraConfiguration.cameraConfiguration;

@Service
public class CameraService
{
    private MediaRepository mediaRepository;
    private LightService lightService;
    private EventRepository eventRepository;

    @Autowired
    public CameraService(
        MediaRepository mediaRepository,
        LightService lightService,
        EventRepository eventRepository)
    {
        this.mediaRepository = mediaRepository;
        this.lightService = lightService;
        this.eventRepository = eventRepository;
    }

    public byte[] snapAndReturn() throws Exception
    {
        return snap();
    }

    private byte[] snap() throws Exception
    {
        lightService.on();
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
            lightService.off();
            return handler.result();
        }
        finally
        {
            lightService.off();
        }
    }

    /**
     * record a gif and save it
     * @return
     * @throws Exception
     */
    @Async
    public void recordAndSaveGif(String filename) throws Exception
    {
        int msBetweenFrames = 200;
        int numFrames = 100;

        CameraConfiguration config = cameraConfiguration()
            .width(600)
            .height(400)
            .encoding(Encoding.GIF)
            .quality(50)
            .rotation(90);

        ByteArrayPictureCaptureHandler handler = new ByteArrayPictureCaptureHandler();

        try(Camera camera = new Camera(config))
        {
            lightService.on();

            eventRepository.push("Warming up camera...");
            Thread.sleep(3000);

            ByteArrayOutputStream gifByteStream = new ByteArrayOutputStream();
            ImageOutputStream imageOutputStream = new FileImageOutputStream(
                new File(mediaRepository.getFullFilename(filename)));

            GifSequenceWriter writer = new GifSequenceWriter(imageOutputStream, 5, msBetweenFrames, true);

            eventRepository.push("Starting capture...");
            List<byte[]> imageBytesList = new ArrayList<>();
            for (int i = 0; i < numFrames; i++)
            {
                camera.takePicture(handler);
                imageBytesList.add(handler.result());
            }

            lightService.off();
            eventRepository.push("Images captured, generating GIF...");

            for(int i = 0; i < imageBytesList.size(); i++)
            {
                InputStream in = new ByteArrayInputStream(imageBytesList.get(i));
                BufferedImage bImageFromConvert = ImageIO.read(in);
                writer.writeToSequence(bImageFromConvert);
            }

            imageOutputStream.seek(0);
            while (true) {
                try {
                    gifByteStream.write(imageOutputStream.readByte());
                } catch (EOFException e) {
                    break;
                } catch (IOException e) {
                    System.out.println("Error processing the Image Stream");
                    break;
                }
            }

            writer.close();
            gifByteStream.close();
            imageOutputStream.close();

            eventRepository.push("New gif available! " + filename);
        }
        catch (Exception e)
        {
            eventRepository.push("There seems to be an issue with the camera... %s", e.getMessage());
            lightService.off();
        }
    }

    public byte[] getGif() throws Exception
    {
        int msBetweenFrames = 200;
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

            List<byte[]> imageBytesList = new ArrayList<>();

            for (int i = 0; i < numFrames; i++)
            {
                camera.takePicture(handler);
                imageBytesList.add(handler.result());
            }

            System.out.println("Photos captured, generating GIF...");

            for(int i = 0; i < imageBytesList.size(); i++)
            {
                InputStream in = new ByteArrayInputStream(imageBytesList.get(i));
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
