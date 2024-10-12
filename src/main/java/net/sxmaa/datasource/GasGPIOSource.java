package net.sxmaa.datasource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GasGPIOSource {

    public static final int DIGITAL_INPUT_PIN = 16;


    private boolean last_state = false;
    private long current_count = 0;
    private ScheduledExecutorService scheduler;

    public GasGPIOSource() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            boolean state = readPIN();
            if (state != last_state) {
                System.out.println("State changed! Current count: " + current_count);
                current_count = current_count + 1;
            }
            last_state = state;
        }, 1, 10L, TimeUnit.MILLISECONDS);
    }

    public void disable() {
        scheduler.shutdownNow();
    }

    public Long getAndResetGasCount() {
        final long temp_store = current_count;
        current_count = 0;
        return temp_store;
    }

    private Boolean readPIN() {
        boolean result = false;
        Runtime r = Runtime.getRuntime();
        Process p = null;
        try {
            p = r.exec("pinctrl get " + DIGITAL_INPUT_PIN);
            p.waitFor();
        } catch (Exception e) {
            System.err.println("Error while reading GPIO Pin " + DIGITAL_INPUT_PIN + ": " + e.getMessage());
        }

        if (p != null) {
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            try {
                while ((line = b.readLine()) != null) {
                    sb.append(line);
                }

                b.close();

                Pattern pattern = Pattern.compile("\\d{1,2}:\\s*ip\\s*pd\\s*\\|\\s*(hi|lo)\\s//\\s*GPIO\\d{1,2}\\s*=\\s*input");
                Matcher matcher = pattern.matcher(sb.toString());

                if (matcher.find(1)) {
                    String state = matcher.group(1);
                    return state.equals("lo");
                }
            } catch (Exception e) {
                System.out.println("Error while sending system command: " + e.getMessage());
            }

        }
        return result;
    }
}
