package com.treatsboot.services;

import org.springframework.stereotype.Service;
import uk.co.caprica.picam.ByteArrayPictureCaptureHandler;
import uk.co.caprica.picam.Camera;
import uk.co.caprica.picam.CameraConfiguration;
import uk.co.caprica.picam.enums.Encoding;

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
}
