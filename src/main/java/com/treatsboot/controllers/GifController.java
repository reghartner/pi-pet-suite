package com.treatsboot.controllers;

import com.treatsboot.repositories.MediaRepository;
import com.treatsboot.services.CameraService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.Instant;

import static java.lang.String.format;

@RestController
@CrossOrigin(origins = "*", exposedHeaders = "Content-Disposition")
@RequestMapping(value = "/api")
@Api(value = "RewardController")
public class GifController
{

    private CameraService cameraService;
    private MediaRepository mediaRepository;

    @Autowired
    public GifController(CameraService cameraService, MediaRepository mediaRepository)
    {
        this.cameraService = cameraService;
        this.mediaRepository = mediaRepository;
    }

    @ApiOperation(value = "Takes a picture and returns it to the browser as a GIF")
    @RequestMapping(
        value = "/snap",
        method = RequestMethod.GET,
        produces = MediaType.IMAGE_GIF_VALUE)
    public HttpEntity<byte[]> snap() throws Exception
    {
        String filename = format("%s_recordGif.gif", Date.from(Instant.now()));
        byte[] picBytes = cameraService.snapAndReturn();
        return new HttpEntity<>(picBytes);
    }

    @ApiOperation(value = "Asynchronously records a GIF and returns the filename for retrieval")
    @RequestMapping(
        value = "/recordGif",
        method = RequestMethod.POST)
    public HttpEntity<String> recordGif() throws Exception
    {
        String filename = format("%s_recordGif.gif", Date.from(Instant.now()));
        cameraService.recordAndSaveGif(filename);
        return new HttpEntity<>(filename);
    }

    @ApiOperation(value = "Get an archived GIF")
    @RequestMapping(
        value = "/gifArchive/{filename}",
        method = RequestMethod.GET,
        produces = MediaType.IMAGE_GIF_VALUE)
    public HttpEntity<byte[]> getMedia(@PathVariable String filename) throws Exception
    {
        return new HttpEntity<>(mediaRepository.getMedia(filename));
    }

    @ApiOperation(value = "Get the latest archived GIF")
    @RequestMapping(
        value = "/gifArchive/latest",
        method = RequestMethod.GET,
        produces = MediaType.IMAGE_GIF_VALUE)
    public HttpEntity<byte[]> getMedia() throws Exception
    {
        return new HttpEntity<>(mediaRepository.getLatestMedia());
    }
}
