#!/usr/bin/env python
from collections import deque
import time
import math
import atexit
import camera
import treat_dispenser
import listener
import sys
import random

def dispense_every_n_minutes(minutes):
    print ("Listening for Harley...will dispense treats after every continuous " + str(minutes) + " minute period of silence")
    listener.execute_every_n_seconds(minutes * 60, listener_callback)
            
def listener_callback():
    dispense_and_snap('Resetting timer.  will dispense another treat after ' + str(minutes) + ' of silence')

def dispense_and_snap(message):
    treat_dispenser.dispense(random.choice([0,1]))
    camera.snap('HarleyCam')
    print(message)

if len(sys.argv) > 1:
    minutes = float(sys.argv[1])
    dispense_every_n_minutes(minutes)
else:
    dispense_and_snap('Done.')