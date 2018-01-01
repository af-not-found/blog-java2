package net.afnf.blog.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;

@Configuration
class MetricsConfig {

    @Autowired
    private MeterRegistry registry;

    @PostConstruct
    private void instrumentDataSource() {
        new JvmThreadMetrics().bindTo(registry);
        new JvmGcMetrics().bindTo(registry);
        new ClassLoaderMetrics().bindTo(registry);
        new FileDescriptorMetrics().bindTo(registry);
        
        // 以下のMetricsは自動追加
        // DataSourcePoolMetrics
        // JvmMemoryMetrics
        // LogbackMetrics
        // UptimeMetrics
    }
}