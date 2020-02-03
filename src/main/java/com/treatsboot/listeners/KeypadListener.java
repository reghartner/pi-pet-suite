package com.treatsboot.listeners;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.springframework.stereotype.Service;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class KeypadListener
{
    /** The Constant PINMAPPING. */
    private static final HashMap<String, Integer> PINMAPPING = new HashMap<String, Integer>();

    /** The gpio. */
    private final GpioController theGpio = GpioFactory.getInstance();

    private List<PropertyChangeListener> listener = new ArrayList<PropertyChangeListener>();
    public static final String KEY = "key";
    private char keyPressed;

    /** The Constant KEYPAD. */
    private static final char keypad[][] = {
        { '1', '2', '3', 'A' },
        { '4', '5', '6', 'B' }, { '7', '8', '9', 'C' },
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

    /**
     * Instantiates a new piezo keypad.
     */
    public KeypadListener() {
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
        // now test the inuts by setting the outputs from high to low
        // one by one
        for (int myO = 0; myO < theOutputs.length; myO++) {
            for (final GpioPinDigitalOutput myTheOutput : theOutputs) {
                myTheOutput.high();
            }

            theOutputs[myO].low();

            // input found?
            if (theInput.isLow()) {
                theCol = myO;
                checkPins();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                }
                break;
            }
        }

        for (final GpioPinDigitalOutput myTheOutput : theOutputs) {
            myTheOutput.low();
        }
    }

    /**
     * Check pins.
     *
     * Determins the pressed key based on the activated GPIO pins.
     */
    private synchronized void checkPins() {

        notifyListeners(KEY,
            this.keyPressed,
            this.keyPressed = keypad[theLin - 1][theCol]);

        System.out.println(keypad[theLin - 1][theCol]);
    }

    /**
     * Inits the listeners.
     */
    private void initListeners() {
        thePin1.addListener((GpioPinListenerDigital) aEvent -> {
            if (aEvent.getState() == PinState.LOW) {
                theInput = thePin1;
                theLin = 1;
                findOutput();
            }
        });
        thePin2.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(
                final GpioPinDigitalStateChangeEvent aEvent) {
                if (aEvent.getState() == PinState.LOW) {
                    theInput = thePin2;
                    theLin = 2;
                    findOutput();
                }
            }
        });
        thePin3.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(
                final GpioPinDigitalStateChangeEvent aEvent) {
                if (aEvent.getState() == PinState.LOW) {
                    theInput = thePin3;
                    theLin = 3;
                    findOutput();
                }
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

    private void notifyListeners(String property, char oldValue,
        char newValue) {
        for (PropertyChangeListener name : listener) {
            name.propertyChange(new PropertyChangeEvent(this, property,
                oldValue, newValue));
        }
    }

    public void addChangeListener(PropertyChangeListener newListener) {
        listener.add(newListener);
    }
    }
}