package net.sxmaa;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.sxmaa.datasource.TasmotaSource.getTasmotaData;

public class MetricCollector extends Collector implements Collector.Describable{


    @Override
    public List<MetricFamilySamples> collect() {
        //System.out.print("[" + new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(new java.util.Date()) + "] ");
        //System.out.println("Started collecting metrics");

        MetricFamilySamples sml_list = this.collectSMLList();
        MetricFamilySamples gpio_count = this.collectGPIOCounter();
        ArrayList<MetricFamilySamples> metrics = new ArrayList<>(
                1 /* sml_list */ +
                1 /* gpio_count */
        );

        metrics.add(sml_list);
        metrics.add(gpio_count);

        //System.out.print("[" + new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(new java.util.Date()) + "] ");
        //System.out.println("Metrics were collected");
        return metrics;
    }

    private MetricFamilySamples collectSMLList() {
        GaugeMetricFamily metric = newSMLListMetric();

        final JsonObject j = getTasmotaData();
        for ( Map.Entry<String, JsonElement> entry : j.entrySet() ) {
            //System.out.println("Entry is " + entry.toString());
            if (!entry.getKey().equals("server_id")) {
                metric.addMetric(List.of(entry.getKey()), entry.getValue().getAsDouble());
            }
        }

        return metric;
    }

    private MetricFamilySamples collectGPIOCounter() {
        GaugeMetricFamily metric = newGPIOGaugeMetric();

        metric.addMetric(List.of("Gas GPIO 23"), Main.gpioSource.getAndResetGasCount());
        return metric;
    }


    @Override
    public List<MetricFamilySamples> describe() {
        List<MetricFamilySamples> descs = new ArrayList<>();
        descs.add(newSMLListMetric());

        return descs;
    }

    private static GaugeMetricFamily newSMLListMetric() {
        return new GaugeMetricFamily(
                "sml_list",
                "List of Tasmoto SML readouts",
                List.of("type")
        );
    }

    private static GaugeMetricFamily newGPIOGaugeMetric() {
        return new GaugeMetricFamily(
                "gpio_gauge",
                "gpio port readouts",
                List.of("type")
        );
    }


}
