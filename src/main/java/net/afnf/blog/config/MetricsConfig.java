package net.afnf.blog.config;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.jdbc.DataSourcePoolMetrics;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.JvmGcMetrics;
import io.micrometer.core.instrument.binder.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.LogbackMetrics;
import io.micrometer.core.instrument.binder.UptimeMetrics;

@Configuration
class MetricsConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Collection<DataSourcePoolMetadataProvider> metadataProviders;

    @Autowired
    private MeterRegistry registry;

    @PostConstruct
    private void instrumentDataSource() {
        new DataSourcePoolMetrics(dataSource, metadataProviders, "DataSource", null).bindTo(registry);
        new JvmThreadMetrics().bindTo(registry);
        new JvmMemoryMetrics().bindTo(registry);
        new JvmGcMetrics().bindTo(registry);
        new LogbackMetrics().bindTo(registry);
        new UptimeMetrics().bindTo(registry);
        new ClassLoaderMetrics().bindTo(registry);
    }
}