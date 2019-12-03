# Treat Dispenser!
Spring Boot application with a (super) simple UI designed for a Raspberry Pi controlled Treat Dispenser.

API allows for:
* Taking a picture via the pi camera
* Dispensing treats via a GPIO controlled motor
* Triggering treats after some period of silence detected by a connected microphone

# Purchase List
* [Treat Dispenser - $13](https://www.amazon.com/gp/product/B000NW5RRG/ref=ppx_yo_dt_b_search_asin_title?ie=UTF8&psc=1)
* [CanaKit Raspberry Pi 3 - $46](https://www.amazon.com/gp/product/B01C6FFNY4/ref=ppx_yo_dt_b_search_asin_title?ie=UTF8&psc=1)
* [12V Stepper Motor w/ driver - $14 (for a set of 5)](https://www.amazon.com/gp/product/B01J507JGS/ref=ppx_yo_dt_b_search_asin_title)
* [12V Power Supply - $11](https://www.amazon.com/gp/product/B01461MOGQ/ref=ppx_yo_dt_b_asin_title_o01_s01?ie=UTF8&psc=1)
* [5mm to 8mm Shaft Coupling - $2](https://usa.banggood.com/5mm-x-8mm-Aluminum-Flexible-Shaft-Coupling-OD19mm-x-L25mm-CNC-Stepper-Motor-Coupler-Connector-p-994359.html?utm_design=41&utm_source=emarsys&utm_medium=Neworder171109&utm_campaign=trigger-emarsys&utm_content=winna&sc_src=email_2675773&sc_eh=0ce0fced42b922ed1&sc_llid=17727428&sc_lid=105229698&sc_uid=1dXmy08CP7&cur_warehouse=CN)
* [Pi Camera - $9](https://www.amazon.com/gp/product/B07QNSJ32M/ref=ppx_yo_dt_b_search_asin_title?ie=UTF8&psc=1)
* [Mini USB Mic - $8](https://www.amazon.com/gp/product/B0138HETXU/ref=ppx_yo_dt_b_search_asin_title?ie=UTF8&psc=1)

Total ~$100, or ~$55 if you have a raspberry pi lying around

# Running the App
I would only expect this to startup if running on a pi, otherwise the GPIO pin initialization will probably fail.

Install Java 11 SDK

`mvn spring-boot:run` and off she goes!

# Scripts
Also included are some python scripts which are capable of most of the same functions as this application, but can be run on demand without a service.  You will probably have to resole a bunch of dependencies to get them to work...
* python3
* pyaudio
* picamera
* RPi.GPIO 

# Building the Treat Dispenser
I am a hardware noob, so I'm sure some folks will come up with way better construction methods... 

* Saw off the shaft of the treat dispenser knob (knob in the trash)
* Attach to the motor with the shaft coupling
* Connect the mic and camera which just pop in
* Connect the motor to the driver and the driver to the pi GPIO pins
  * Use pins 7, 11, 13, and 15 (what the pi calls them)
  * In the code, these map to GPIO pins 0, 7, 2, and 3 respectively
  * If this is confusing, which it is, consult this diagram: [Pi Pin Layouts](https://pi4j.com/1.2/pins/model-3b-rev1.html)
* Figure out how to mount the motor...I ended up fastening 2 outward facing bolts onto the dispenser which allows for easy removal/replacement
