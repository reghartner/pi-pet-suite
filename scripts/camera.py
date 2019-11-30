#!/usr/bin/env python
import time
import math
import picamera
import sys

CAM_WARMUP_TIME = 2

def snap(prefix):
    with picamera.PiCamera() as camera:
        filename = './images/' + prefix + str(time.time()) + '.jpg'
        camera.resolution = (1024, 768)
        camera.start_preview()
        time.sleep(CAM_WARMUP_TIME)
        camera.rotation = 270
        camera.capture(filename)
        print ('Image captured! ' + filename)