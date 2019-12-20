package com.treatsboot.controllers;

import com.treatsboot.services.RewardService;
import com.treatsboot.services.TreatDispenserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", exposedHeaders = "Content-Disposition")
@RequestMapping(value = "/api")
@Api(value = "RewardController")
public class RewardController
{
    private TreatDispenserService treatDispenserService;
    private RewardService rewardService;

    @Autowired
    public RewardController(TreatDispenserService treatDispenserService, RewardService rewardService)
    {
        this.treatDispenserService = treatDispenserService;
        this.rewardService = rewardService;
    }

    @ApiOperation(value = "Asynchronously dispense treats and records a GIF.  Returns the filename for retrieval")
    @RequestMapping(
        value = "/reward",
        method = RequestMethod.POST)
    public HttpEntity<String> treat(
        @RequestParam(required = false, defaultValue = "false") boolean smallTreat)
        throws Exception
    {
        String filename = rewardService.dispenseAndRecord(smallTreat);
        return new HttpEntity<>(filename);
    }

    @ApiOperation(value =
        "Reward with some treats after some minutes of silence.  Records a short gif when dispensing.  "
            + "Returns the name of the file that will be generated for retrieval")
    @RequestMapping(
        value = "/reward/after/{minutes}",
        method = RequestMethod.POST)
    public HttpEntity<String> rewardForSilence(
        @PathVariable double minutes,
        @RequestParam(required = false, defaultValue = "false") boolean smallTreat)
        throws Exception
    {
        String filename = rewardService.rewardForSilence(minutes, smallTreat);
        return new HttpEntity<>(filename);
    }

    @ApiOperation(value = "Reward with some treats after some minutes of silence.  Repeats until a max time.  Will record GIFs")
    @RequestMapping(
        value = "/reward/after/{minutes}/until/{totalMinutes}",
        method = RequestMethod.POST)
    public HttpStatus rewardForSilenceOverTime(
        @PathVariable double minutes,
        @PathVariable double totalMinutes)
        throws Exception
    {
        rewardService.rewardForSilenceOverTime(minutes, totalMinutes);
        return HttpStatus.OK;
    }
}
