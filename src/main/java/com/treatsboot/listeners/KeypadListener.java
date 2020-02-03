package com.treatsboot.listeners;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.treatsboot.services.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class KeypadListener
{
    /** The gpio. */
    private final GpioController theGpio = GpioFactory.getInstance();

    /** The Constant KEYPAD. */
    private static final char keypad[][] = {
        { '1', '2', '3', 'A' },
        { '4', '5', '6', 'B' },
        { '7', '8', '9', 'C' },
        { '*', '0', '#', 'D' } };

    private static final Pin PIN_1_IN = RaspiPin.GPIO_15;
    private static final Pin PIN_2_IN = RaspiPin.GPIO_16;
    private static final Pin PIN_3_IN = RaspiPin.GPIO_01;
    private static final Pin PIN_4_IN = RaspiPin.GPIO_04;
    private static final Pin PIN_5_OUT = RaspiPin.GPIO_05;
    private static final Pin PIN_6_OUT = RaspiPin.GPIO_06;
    private static final Pin PIN_7_OUT = RaspiPin.GPIO_10;
    private static final Pin PIN_8_OUT = RaspiPin.GPIO_11;

    private final GpioPinDigitalInput thePin1 = theGpio.provisionDigitalInputPin(PIN_1_IN, PinPullResistance.PULL_UP);
    private final GpioPinDigitalInput thePin2 = theGpio.provisionDigitalInputPin(PIN_2_IN, PinPullResistance.PULL_UP);
    private final GpioPinDigitalInput thePin3 = theGpio.provisionDigitalInputPin(PIN_3_IN, PinPullResistance.PULL_UP);
    private final GpioPinDigitalInput thePin4 = theGpio.provisionDigitalInputPin(PIN_4_IN, PinPullResistance.PULL_UP);
    private final GpioPinDigitalOutput thePin5 = theGpio.provisionDigitalOutputPin(PIN_5_OUT);
    private final GpioPinDigitalOutput thePin6 = theGpio.provisionDigitalOutputPin(PIN_6_OUT);
    private final GpioPinDigitalOutput thePin7 = theGpio.provisionDigitalOutputPin(PIN_7_OUT);
    private final GpioPinDigitalOutput thePin8 = theGpio.provisionDigitalOutputPin(PIN_8_OUT);
    private final GpioPinDigitalOutput theOutputs[] = { thePin5, thePin6, thePin7, thePin8 };

    private GpioPinDigitalInput theInput;
    private int theLin;
    private int theCol;
    private RewardService rewardService;

    /**
     * Instantiates a new piezo keypad.
     */
    @Autowired
    public KeypadListener(RewardService rewardService) {
        this.rewardService = rewardService;
        initListeners();
    }

    /**
     * Find output.
     *
     * Sets output lines to high and then to low one by one. Then the input line
     * is tested. If its state is low, we have the right output line and
     * therefore a mapping to a key on the keypad.
     */
    private void findOutput() {
        // now test the inputs by setting the outputs from high to low
        // one by one
        for (int myO = 0; myO < theOutputs.length; myO++) {
            for (final GpioPinDigitalOutput myTheOutput : theOutputs) {
                myTheOutput.high();
            }

            theOutputs[myO].low();

            // input found?
            if (theInput.isLow())
            {
                theCol = myO;
                handleInput();
                try
                {
                    Thread.sleep(200);
                }
                catch (InterruptedException e) {

                }
            }
        }

        for (final GpioPinDigitalOutput myTheOutput : theOutputs)
        {
            myTheOutput.low();
        }
    }

    /**
     * Handle the input from the keypad
     */
    private void handleInput()
    {
        char pressed = keypad[theLin - 1][theCol];
        try
        {
            Integer intInput = Integer.parseInt("" + pressed);
            System.out.println(format("Pressed: %s", intInput));
            if(intInput == 0)
            {
                rewardService.rewardForSilence(intInput, true);
            }
            else if (intInput > 0)
            {
                rewardService.dispenseAndRecord(true);
            }
        }
        catch (Exception e)
        {
            System.out.println(format("Not an integer: %s", pressed));
        }
    }

    private void initListeners()
    {
        thePin1.addListener((GpioPinListenerDigital) aEvent -> {
            if (aEvent.getState() == PinState.LOW) {
                theInput = thePin1;
                theLin = 1;
                findOutput();
            }
        });
        thePin2.addListener((GpioPinListenerDigital) aEvent -> {
            if (aEvent.getState() == PinState.LOW) {
                theInput = thePin2;
                theLin = 2;
                findOutput();
            }
        });
        thePin3.addListener((GpioPinListenerDigital) aEvent -> {
            if (aEvent.getState() == PinState.LOW) {
                theInput = thePin3;
                theLin = 3;
                findOutput();
            }
        });
        thePin4.addListener((GpioPinListenerDigital) aEvent -> {
            if (aEvent.getState() == PinState.LOW) {
                theInput = thePin4;
                theLin = 4;
                findOutput();
            }
        });
    }
}