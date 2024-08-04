package net.sxmaa.datasource;

import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputProvider;
import com.pi4j.io.gpio.digital.PullResistance;

import java.text.SimpleDateFormat;

public class GasGPIOSource {

    public static final int DIGITAL_INPUT_PIN = 23;
    private DigitalInput INPUT;

    private long last_millis;
    private long current_count = 0;

    public GasGPIOSource() {
           initGPIOProvider();
           initGPIOListener();
    }

    private void initGPIOProvider() {
        var pi4j = Pi4J.newAutoContext();

        // create a digital input instance using the default digital input provider
        // we will use the PULL_DOWN argument to set the pin pull-down resistance on this GPIO pin
        var config = DigitalInput.newConfigBuilder(pi4j)
                //.id("my-digital-input")
                .address(DIGITAL_INPUT_PIN)
                .pull(PullResistance.PULL_DOWN)
                .build();

        // get a Digital Input I/O provider from the Pi4J context
        DigitalInputProvider digitalInputProvider = pi4j.provider("pigpio-digital-input");

        INPUT = digitalInputProvider.create(config);

        last_millis = System.currentTimeMillis();
    }

    private void initGPIOListener() {
        // setup a digital output listener to listen for any state changes on the digital input
        INPUT.addListener(event -> {
            Integer count = (Integer) event.source().metadata().get("count").value();
            System.out.println(event + " === " + count);
            // only count this value
            if (System.currentTimeMillis() - last_millis >= 100) {
                System.out.println("[" +
                        new SimpleDateFormat("dd.MM.yy HH:mm:ss")
                                .format(new java.util.Date()) +
                        "] We would have only counted this: " + count
                );
                current_count++;
            }
        });
    }

    public Long getAndResetGasCount() {
        final long temp_store = current_count;
        current_count = 0;
        return temp_store;
    }
}
