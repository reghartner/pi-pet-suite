package com.treatsboot.controllers;

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

import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins = "*", exposedHeaders = "Content-Disposition")
@RequestMapping(value = "/api")
@Api(value = "TreatsController")
public class TreatsController
{

    private CameraService cameraService;
    private TreatDispenserService treatDispenserService;
    private RewardService rewardService;

    @Autowired
    public TreatsController(
        CameraService cameraService,
        TreatDispenserService treatDispenserService,
        RewardService rewardService)
    {
        this.cameraService = cameraService;
        this.treatDispenserService = treatDispenserService;
        this.rewardService = rewardService;
    }

    @ApiOperation(value = "Snap a picture and return as an attachment")
    @RequestMapping(
        produces= MediaType.IMAGE_JPEG_VALUE,
        value = "/snapDownload",
        method = RequestMethod.GET)
    public HttpEntity<byte[]> snapDownload(HttpServletResponse response) throws Exception
    {
        byte[] picBytes = cameraService.snap();
        response.setHeader("Content-Disposition", "attachment; filename=test.jpg");

        return new HttpEntity<>(picBytes);
    }

    @ApiOperation(value = "Dispense some treats and take a picture returned as an attachment")
    @RequestMapping(
        produces= MediaType.IMAGE_JPEG_VALUE,
        value = "/treatPicDownload",
        method = RequestMethod.GET)
    public HttpEntity<byte[]> treatPicDownload(HttpServletResponse response) throws Exception
    {
        byte[] picBytes = rewardService.dispenseAndSnap();
        response.setHeader("Content-Disposition", "attachment; filename=test.jpg");

        return new HttpEntity<>(picBytes);
    }

    @ApiOperation(value = "Snap a picture")
    @RequestMapping(
        produces= MediaType.IMAGE_JPEG_VALUE,
        value = "/snap",
        method = RequestMethod.GET)
    public HttpEntity<byte[]> snap() throws Exception
    {
        byte[] picBytes = cameraService.snap();
        return new HttpEntity<>(picBytes);
    }

    @ApiOperation(value = "Get a quick video")
    @RequestMapping(
        produces= MediaType.IMAGE_GIF_VALUE,
        value = "/gif",
        method = RequestMethod.GET)
    public HttpEntity<byte[]> gif() throws Exception
    {
        byte[] picBytes = cameraService.getGif();
        return new HttpEntity<>(picBytes);
    }

    @ApiOperation(value = "Dispense some treats and take a picture")
    @RequestMapping(
        produces= MediaType.IMAGE_JPEG_VALUE,
        value = "/treatPic",
        method = RequestMethod.GET)
    public HttpEntity<byte[]> treatPic() throws Exception
    {
        byte[] picBytes = rewardService.dispenseAndSnap();
        return new HttpEntity<>(picBytes);
    }

    @ApiOperation(value = "Reward with some treats after some minutes of silence")
    @RequestMapping(value = "/rewardForSilence/{minutes}", method = RequestMethod.POST)
    public HttpStatus rewardForSilence(
        @PathVariable double minutes,
        @RequestParam(required = false, defaultValue = "false") boolean smallTreat)
        throws InterruptedException
    {
        rewardService.rewardForSilence(minutes, smallTreat);
        return HttpStatus.OK;
    }

    @ApiOperation(value = "Reward with some treats after some minutes of silence.  Repeats until a max time.")
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
    public HttpStatus dispense() throws Exception
    {
        treatDispenserService.treat();
        return HttpStatus.OK;
    }
}
