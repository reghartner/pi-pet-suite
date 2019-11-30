package com.treatsboot.services;

import com.pi4j.io.gpio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import static com.pi4j.io.gpio.PinState.LOW;
import static com.pi4j.io.gpio.PinState.HIGH;

/**
 * Represents the 28BYJ-48 stepper motor, and the ULN2003 driver board connected
 * to a Raspberry Pi. It uses Pi4J to set the I/O pins on the Raspberry Pi. This
 * class supports the stepping methods 'wave drive', 'full step' and 'half step'
 * (see <https://www.youtube.com/watch?v=B86nqDRskVU> for details.)
 *
 * @see <http://pi4j.com>
 */
@Service
public class MotorDemo
{

    private static final Logger LOG = LoggerFactory.getLogger(MotorDemo.class);

    private final GpioController gpio = GpioFactory.getInstance();

    /**
     * The stepping method to be used by the motor.
     *
     * @see <https://www.youtube.com/watch?v=B86nqDRskVU>
     */
    public enum SteppingMethod {
        WAVE_DRIVE, FULL_STEP, HALF_STEP
    };

    /**
     * The electric potential sequence to be applied to the motor in
     * wave drive mode.
     * @see <https://www.youtube.com/watch?v=B86nqDRskVU>
     */
    private static final PinState WAVE_DRIVE_MOTOR_SEQUENCE[][] = new PinState[][] {
        { LOW,  LOW,  LOW,  HIGH },
        { LOW,  LOW,  HIGH, LOW },
        { LOW,  HIGH, LOW,  LOW },
        { HIGH, LOW,  LOW,  LOW },
        { LOW,  LOW,  LOW,  HIGH },
        { LOW,  LOW,  HIGH, LOW },
        { LOW,  HIGH, LOW,  LOW },
        { HIGH, LOW,  LOW,  LOW }
    };

    /**
     * The electric potential sequence to be applied to the motor in
     * full step mode.
     * @see <https://www.youtube.com/watch?v=B86nqDRskVU>
     */
    private static final PinState FULL_STEP_MOTOR_SEQUENCE[][] = new PinState[][] {
        { LOW,  LOW,  LOW,  HIGH },
        { LOW,  LOW,  HIGH, LOW },
        { LOW,  HIGH, LOW,  LOW },
        { HIGH, LOW,  LOW,  LOW },
        { LOW,  LOW,  LOW,  HIGH },
        { LOW,  LOW,  HIGH, LOW },
        { LOW,  HIGH, LOW,  LOW },
        { HIGH, LOW,  LOW,  LOW }
    };

    /**
     * The electric potential sequence to be applied to the motor in
     * half step mode.
     * @see <https://www.youtube.com/watch?v=B86nqDRskVU>
     */
    private static final PinState HALF_STEP_MOTOR_SEQUENCE[][] = new PinState[][] {
        { LOW,  LOW,  LOW,  HIGH },
        { LOW,  LOW,  HIGH, HIGH },
        { LOW,  LOW,  HIGH, LOW },
        { LOW,  HIGH, HIGH, LOW },
        { LOW,  HIGH, LOW,  LOW },
        { HIGH, HIGH, LOW,  LOW },
        { HIGH, LOW,  LOW,  LOW },
        { HIGH, LOW,  LOW,  HIGH }
    };

    /** The current step duration in milliseconds (i.e. pause betwenn steps). **/
    private int stepDuration;

    /** Holds the Raspberry Pi pin numbers for pins A though D of the stepper motor. **/
    private GpioPinDigitalOutput[] motorPins;

    /** The current stepping method of this motor **/
    private SteppingMethod steppingMethod;

    public MotorDemo()
    {
        this.stepDuration = 10;
        this.steppingMethod = SteppingMethod.HALF_STEP;
    }

    private void initPins()
    {
        cleanupPins();
        provisionPins();
        gpio.setShutdownOptions(true, PinState.LOW, motorPins);
    }

    private void provisionPins()
    {
        motorPins = new GpioPinDigitalOutput[]
        {
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "Pin A", LOW),
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "Pin B", LOW),
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "Pin C", LOW),
            gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "Pin D", LOW)
        };

        gpio.setShutdownOptions(true, PinState.LOW, motorPins);
    }

    private void cleanupPins()
    {
        safeUnprovision(RaspiPin.allPins());
    }

    private void safeUnprovision(Pin... pins)
    {
        for(Pin pin: pins)
        {
            try
            {
                gpio.unprovisionPin(gpio.getProvisionedPin(pin));
            }
            catch (Exception e)
            {}
        }
    }

    /**
     * Rotates the motor by a specified angle. Note that the step angle
     * of the motor is 0.19 degrees in full step and wave drive methods
     * and 0.09 degrees in half step method.
     *
     * @param angle - the angle in degrees
     */
    public void angleRotation(float angle) {
        int steps;
        switch (steppingMethod) {
            case HALF_STEP:
                steps = (int) (512 * 8 * angle) / 360;
                break;
            default:
                steps = (int) (512 * 4 * angle) / 360;
                break;
        }
        step(steps);
    }

    /**
     * Moves the stepper motor by one step. Depending on the stepping method
     * this will move the motor by 0.19 degrees in case of full step and wave drive methods
     * or 0.09 degrees in half step method. If the passed value is positive the motor
     * will rotate clockwise, if negative it will rotate counterclockwise.
     */
    private void step(int noOfSteps)
    {
        initPins();
        if (noOfSteps > 0)
        {
            for (int currentStep = noOfSteps; currentStep > 0; currentStep--)
            {
                int currentSequenceNo = currentStep % 8;
                writeSequence(currentSequenceNo);
            }
        }
        else
            {
            for (int currentStep = 0; currentStep < Math.abs(noOfSteps); currentStep++)
            {
                int currentSequenceNo = currentStep % 8;
                writeSequence(currentSequenceNo);
            }
        }
        gpio.shutdown();
        cleanupPins();
    }

    /**
     * Performs a demo of the various methods to move the motor.
     */
    public void performDemo()
    {
        LOG.info("Full rotation clockwise in wave drive method... ");
        this.steppingMethod = SteppingMethod.WAVE_DRIVE;
        angleRotation(360);
        LOG.info("Done.");

        LOG.info("Full rotation counterclockwise in full step method... ");
        this.steppingMethod = SteppingMethod.FULL_STEP;
        angleRotation(-360);
        LOG.info("Done.");

        LOG.info("Full rotation clockwise in half step method... ");
        this.steppingMethod = SteppingMethod.HALF_STEP;
        angleRotation(180);
        LOG.info("Done.");

        LOG.info("Half rotation counterclockwise in full step method... ");
        this.steppingMethod = SteppingMethod.FULL_STEP;
        angleRotation(-180);
        LOG.info("Done.");
    }

    /**
     * Writes the motor sequence to the Raspberry Pi pins.
     *
     * @param sequenceNo - references a sequence in one of the motor sequences above.
     */
    private void writeSequence(int sequenceNo) {
        for (int i = 0; i < 4; i++)
        {
            switch(steppingMethod) {
                case WAVE_DRIVE:
                    motorPins[i].setState(WAVE_DRIVE_MOTOR_SEQUENCE[sequenceNo][i]);
                    break;
                case FULL_STEP:
                    motorPins[i].setState(FULL_STEP_MOTOR_SEQUENCE[sequenceNo][i]);
                    break;
                default:
                    motorPins[i].setState(HALF_STEP_MOTOR_SEQUENCE[sequenceNo][i]);
                    break;
            }
        }
        try
        {
            Thread.sleep(stepDuration);
        }
        catch (InterruptedException e) {}
    }
}