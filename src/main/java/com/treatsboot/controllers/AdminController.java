package com.treatsboot.controllers;

import com.treatsboot.repositories.EventRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", exposedHeaders = "Content-Disposition")
@RequestMapping(value = "/api")
@Api(value = "AdminController")
public class AdminController
{
    private EventRepository eventRepository;

    @Autowired
    public AdminController(EventRepository eventRepository)
    {
        this.eventRepository = eventRepository;
    }

    @ApiOperation(value = "Takes a picture and returns it to the browser as a GIF")
    @RequestMapping(
        value = "/restart",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpStatus restart()
    {
        return HttpStatus.OK;
    }
}
