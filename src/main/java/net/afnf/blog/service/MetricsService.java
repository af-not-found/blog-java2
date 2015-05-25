package net.afnf.blog.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    @Autowired
    private Collection<PublicMetrics> publicMetrics;

    public List<Pair<String, String>> getMetricsList() {

        List<Pair<String, String>> list = new ArrayList<Pair<String, String>>();

        Iterator<PublicMetrics> ite1 = publicMetrics.iterator();
        while (ite1.hasNext()) {
            PublicMetrics publicMetrics = (PublicMetrics) ite1.next();
            Iterator<Metric<?>> ite2 = publicMetrics.metrics().iterator();
            while (ite2.hasNext()) {
                Metric<?> metric = (Metric<?>) ite2.next();
                list.add(new MutablePair<String, String>(metric.getName(), metric.getValue().toString()));
            }
        }

        return list;
    }
}
