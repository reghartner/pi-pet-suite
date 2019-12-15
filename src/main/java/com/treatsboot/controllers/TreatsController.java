package com.treatsboot.controllers;

import com.treatsboot.repositories.MediaRepository;
import com.treatsboot.services.CameraService;
import com.treatsboot.services.RewardService;
import com.treatsboot.services.TreatDispenserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.time.Instant;

import static java.lang.String.format;

@RestController
@CrossOrigin(origins = "*", exposedHeaders = "Content-Disposition")
@RequestMapping(value = "/api")
@Api(value = "TreatsController")
public class TreatsController
{

    private CameraService cameraService;
    private TreatDispenserService treatDispenserService;
    private RewardService rewardService;
    private MediaRepository mediaRepository;

    @Autowired
    public TreatsController(
        CameraService cameraService,
        TreatDispenserService treatDispenserService,
        RewardService rewardService,
        MediaRepository mediaRepository)
    {
        this.cameraService = cameraService;
        this.treatDispenserService = treatDispenserService;
        this.rewardService = rewardService;
        this.mediaRepository = mediaRepository;
    }

    @ApiOperation(value = "Takes a picture and returns it to the browser as a JPEG")
    @RequestMapping(
        produces= MediaType.IMAGE_JPEG_VALUE,
        value = "/snapAndReturn",
        method = RequestMethod.GET)
    public HttpEntity<byte[]> snap() throws Exception
    {
        byte[] picBytes = cameraService.snapAndReturn();
        return new HttpEntity<>(picBytes);
    }

    @ApiOperation(value = "Asynchronously records a GIF and returns the filename for retrieval")
    @RequestMapping(
        produces= MediaType.IMAGE_GIF_VALUE,
        value = "/gif",
        method = RequestMethod.GET)
    public HttpEntity<String> gif() throws Exception
    {
        String filename = format("getGif_%s.gif", Date.from(Instant.now()));
        cameraService.recordAndSaveGif(filename);
        return new HttpEntity<>(filename);
    }

    @ApiOperation(value = "Asynchronously dispense treats and records a GIF.  Returns the filename for retrieval")
    @RequestMapping(
        produces= MediaType.IMAGE_GIF_VALUE,
        value = "/treatGif",
        method = RequestMethod.GET)
    public HttpEntity<String> treatGif(@RequestParam(required = false, defaultValue = "false") boolean smallTreat) throws Exception
    {
        String filename = rewardService.dispenseAndRecord(smallTreat);
        return new HttpEntity<>(filename);
    }

    @ApiOperation(value = "Dispense some treats and take a picture.  Returns the picture to the browser as a JPEG")
    @RequestMapping(
        produces= MediaType.IMAGE_GIF_VALUE,
        value = "/treatPic",
        method = RequestMethod.GET)
    public HttpEntity<byte[]> treatPic(@RequestParam(required = false, defaultValue = "false") boolean smallTreat) throws Exception
    {
        byte[] picBytes = rewardService.dispenseAndSnap(smallTreat);
        return new HttpEntity<>(picBytes);
    }

    @ApiOperation(value = "Reward with some treats after some minutes of silence.  Records a short gif when dispensing.  "
        + "Returns the name of the file that will be generated for retrieval")
    @RequestMapping(value = "/rewardForSilence/{minutes}", method = RequestMethod.POST)
    public HttpEntity<String> rewardForSilence(
        @PathVariable double minutes,
        @RequestParam(required = false, defaultValue = "false") boolean smallTreat)
        throws InterruptedException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        String filename = rewardService.rewardForSilence(minutes, smallTreat);
        return new HttpEntity<>(filename);
    }

    @ApiOperation(value = "Reward with some treats after some minutes of silence.  Repeats until a max time.  Will record GIFs")
    @RequestMapping(value = "/rewardForSilenceOverTime/{minutes}/{totalMinutes}", method = RequestMethod.POST)
    public HttpStatus rewardForSilenceOverTime(
        @PathVariable double minutes,
        @PathVariable double totalMinutes) throws Exception
    {
        rewardService.rewardForSilenceOverTime(minutes, totalMinutes);
        return HttpStatus.OK;
    }

    @ApiOperation(value = "Dispense some treats")
    @RequestMapping(value = "/treat", method = RequestMethod.PUT)
    public HttpStatus dispense(@RequestParam(required = false, defaultValue = "false") boolean smallTreat)
    {
        treatDispenserService.treat(smallTreat);
        return HttpStatus.OK;
    }

    @ApiOperation(value = "Dispense some treats and take a picture.  Returns the picture to the browser as a JPEG")
    @RequestMapping(
        produces= MediaType.IMAGE_GIF_VALUE,
        value = "/gifArchive/{filename}",
        method = RequestMethod.GET)
    public HttpEntity<byte[]> getMedia(String filename) throws Exception
    {
        return new HttpEntity<>(mediaRepository.getMedia(filename));
    }
}
