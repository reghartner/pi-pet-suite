#!/usr/bin/env python
import pyaudio
import audioop
from collections import deque
import time
import math
import atexit
import sys

# Microphone stream config.
CHUNK = 1024  # CHUNKS of bytes to read each time from mic
FORMAT = pyaudio.paInt16
CHANNELS = 1
RATE = 44100
THRESHOLD = 350000  # The threshold intensity that defines silence
                  # and noise signal (an int. lower than THRESHOLD is silence).

def execute_every_n_seconds(seconds, callback):
    #Open stream
    p = pyaudio.PyAudio()

    stream = p.open(format=FORMAT,
                    channels=CHANNELS,
                    rate=RATE,
                    input=True,
                    frames_per_buffer=CHUNK)

    def on_exit():
        stream.close()
        p.terminate()
        print ('listener.py exiting...')

    atexit.register(on_exit)
    listening_message = 'Listening for ' + str(seconds / 60) + ' minutes of silence'
    print (listening_message)
    silence_begin_time = time.time()

    while True:
        data = stream.read(CHUNK, exception_on_overflow = False)
        intensity = audioop.rms(data, 4)
        elapsed_time = time.time() - silence_begin_time
        if (intensity > THRESHOLD):
            print ("mic triggered @ " + str(intensity) + " - resetting timer.  Elapsed Time: " + str(elapsed_time//1) + " seconds")
            silence_begin_time = time.time()
        elif (elapsed_time > seconds):
            on_exit()
            callback()
            silence_begin_time = time.time()
        time.sleep(0.001)