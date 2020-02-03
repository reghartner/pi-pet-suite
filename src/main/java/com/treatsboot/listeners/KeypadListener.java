package com.treatsboot.listeners;

import com.pi4j.io.gpio.*;
import com.treatsboot.services.CameraService;
import com.treatsboot.services.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class KeypadListener
{
    /** The Constant PINMAPPING. */
    private static final HashMap<String, Integer> PINMAPPING = new HashMap<String, Integer>();

    /** The gpio. */
    private final GpioController theGpio = GpioFactory.getInstance();

    private static final Pin PIN_OUT_1 = RaspiPin.GPIO_15;
    private static final Pin PIN_OUT_2 = RaspiPin.GPIO_16;
    private static final Pin PIN_IN_1 = RaspiPin.GPIO_01;
    private static final Pin PIN_IN_2 = RaspiPin.GPIO_04;
    private static final Pin PIN_IN_3 = RaspiPin.GPIO_05;
    private static final Pin PIN_IN_4 = RaspiPin.GPIO_06;
    private static final Pin PIN_IN_5 = RaspiPin.GPIO_10;
    private static final Pin PIN_IN_6 = RaspiPin.GPIO_11;

    private GpioPinDigitalOutput out1 = theGpio.provisionDigitalOutputPin(PIN_OUT_1, PinState.LOW);
    private GpioPinDigitalOutput out2 = theGpio.provisionDigitalOutputPin(PIN_OUT_2, PinState.LOW);
    private GpioPinDigitalInput in1 = theGpio.provisionDigitalInputPin(PIN_IN_1, PinPullResistance.PULL_UP);
    private GpioPinDigitalInput in2 = theGpio.provisionDigitalInputPin(PIN_IN_2, PinPullResistance.PULL_UP);
    private GpioPinDigitalInput in3 = theGpio.provisionDigitalInputPin(PIN_IN_3, PinPullResistance.PULL_UP);
    private GpioPinDigitalInput in4 = theGpio.provisionDigitalInputPin(PIN_IN_4, PinPullResistance.PULL_UP);
    private GpioPinDigitalInput in5 = theGpio.provisionDigitalInputPin(PIN_IN_5, PinPullResistance.PULL_UP);
    private GpioPinDigitalInput in6 = theGpio.provisionDigitalInputPin(PIN_IN_6, PinPullResistance.PULL_UP);

    private GpioPinDigitalOutput theOutputs[] = {out1, out2};
    private GpioPinDigitalInput theInputs[] = {in1, in2, in3, in4, in5, in6};

    private int theFirstPin = 0;
    private int theSecondPin = 0;
    private RewardService rewardService;
    private CameraService cameraService;

    @Autowired
    public KeypadListener(RewardService rewardService, CameraService cameraService)
    {
        this.rewardService = rewardService;
        this.cameraService = cameraService;
        initMapping();
        eventLoop();
    }

    /**
     * Event loop.
     */
    private void eventLoop()
    {
        while (true)
        {
            // Just stay inside the loop, but do not consume all CPU
            try
            {
                boolean myKeyPress = false;
                GpioPinDigitalInput myInput = null;
                int myInId = -1;

                // all outputs low
                for (int myO = 0; myO < theOutputs.length; myO++)
                {
                    theOutputs[myO].low();
                }

                while (!myKeyPress)
                {
                    // waiting for input
                    for (int myI = 0; myI < theInputs.length; myI++)
                    {
                        if (theInputs[myI].isLow())
                        {
                            myInId = myI + 4;
                            myInput = theInputs[myI];
                            myKeyPress = true;
                            System.out.print("In = " + myInId);
                            break;
                        }
                    }

                    Thread.sleep(100);
                }

                // now test the inputs by setting the outputs from high to low one by one
                for (int myO = 0; myO < theOutputs.length; myO++)
                {
                    for (int myO2 = 0; myO2 < theOutputs.length; myO2++)
                    {
                        theOutputs[myO2].high();
                    }

                    theOutputs[myO].low();

                    // input found?
                    if (myInput.isLow())
                    {
                        System.out.println("Out = " + myO);

                        theSecondPin = myInId;

                        if (myO == 0)
                        {
                            theFirstPin = 1;
                        }
                        else
                        {
                            theFirstPin = 3;
                        }

                        checkPins();
                        break;
                    }
                }
            }
            catch (InterruptedException myIE)
            {
                myIE.printStackTrace(System.err);
            }
        }
    }

    /**
     * Maps a pin combination on a keypad entry.
     */
    private void initMapping()
    {
        PINMAPPING.put("35", (int) '1');
        PINMAPPING.put("36", (int) '2');
        PINMAPPING.put("37", (int) '3');
        PINMAPPING.put("38", (int) '4');
        PINMAPPING.put("39", (int) '5');
        PINMAPPING.put("14", (int) '6');
        PINMAPPING.put("15", (int) '7');
        PINMAPPING.put("16", (int) '8');
        PINMAPPING.put("17", (int) '9');
        PINMAPPING.put("18", (int) '*');
        PINMAPPING.put("34", (int) '0');
        PINMAPPING.put("19", (int) '#');
    }

    private void checkPins()
    {
        System.out.println("Pin states = " + theFirstPin + " - " + theSecondPin);

        if (theFirstPin != 0 && theSecondPin != 0)
        {
            int myInput = PINMAPPING.get("" + theFirstPin + theSecondPin);

            System.out.println("Input read = " + (char) myInput);

            theFirstPin = 0;
            theSecondPin = 0;
        }
    }
}