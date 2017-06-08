package de.bischinger.vertxprometheus;

import com.codahale.metrics.MetricRegistry;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.vertx.MetricsHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;

import static com.codahale.metrics.SharedMetricRegistries.getOrCreate;
import static io.prometheus.client.CollectorRegistry.defaultRegistry;
import static io.vertx.core.Vertx.vertx;

public class PrometheusVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        MetricRegistry metricRegistry = getOrCreate("exported");
        defaultRegistry.register(new DropwizardExports(metricRegistry));

        //Bind metrics handler to /metrics
        Router router = Router.router(vertx);
        router.get("/metrics").handler(new MetricsHandler());

        //Start httpserver on localhost:8080
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);

        //Increase counter every second
        vertx.setPeriodic(1_000L, e -> metricRegistry.counter("testCounter").inc());
    }

    public static void main(String[] args) {
        vertx().deployVerticle(PrometheusVerticle.class.getName());
    }
}
