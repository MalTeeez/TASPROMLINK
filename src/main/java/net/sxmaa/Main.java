package net.sxmaa;

import io.prometheus.client.exporter.HTTPServer;
import net.sxmaa.datasource.GasGPIOSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.text.SimpleDateFormat;

public class Main {
    private static HTTPServer http_server;

    private static MetricCollector collector;
    public static GasGPIOSource gpioSource;
    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        initGPIOSource();
        initHttpServer();
        initCollectors();


        System.out.println("Tasmota <=> Prometheus Link is now active.");
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            http_server.close();
            System.out.println("Exiting, Goodbye!");
        }
    }

    private static void initHttpServer() throws IOException {
        int port = 19501;
        String address = "0.0.0.0";
        try {
            http_server = new HTTPServer(address, port, true);
            System.out.print(new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(new java.util.Date()));
            System.out.println("HTTPServer listening on http://" + "localhost:" + http_server.getPort() + "/metrics");
        } catch (BindException e) {
            System.out.println("Failed to start prometheus link, port " + port + " already in use.");
        }
    }

    private static void initCollectors() {
        collector = new MetricCollector();
        collector.register();
    }

    private static void initGPIOSource() {
        gpioSource = new GasGPIOSource();
    }

}