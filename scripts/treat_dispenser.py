
#!/usr/bin/env python
import time
import math
import RPi.GPIO as GPIO
import atexit
import random

clockwise_range = range(8)
counter_range = range(7,-1,-1)
ranges = [clockwise_range, counter_range]

def dispense(direction):
    print ("dispensing treats")
    GPIO.setmode(GPIO.BOARD)
    GPIO.setwarnings(False)
    control_pins = [7,11,13,15]
    for pin in control_pins:
        GPIO.setup(pin, GPIO.OUT)
        GPIO.output(pin, 0)
    
    halfstep_seq = [
      [1,0,0,0],
      [1,1,0,0],
      [0,1,0,0],
      [0,1,1,0],
      [0,0,1,0],
      [0,0,1,1],
      [0,0,0,1],
      [1,0,0,1]
    ]

    step_range = ranges[int(direction)]

    for i in range(128):
      for step in step_range:
        for pin in range(4):
          GPIO.output(control_pins[pin], halfstep_seq[step][pin])
        time.sleep(0.008)
    GPIO.cleanup()
