package net.afnf.blog.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.system.DiskSpaceHealthIndicator;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Meter.Id;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;

@Service
public class MetricsService {

    private static final Log logger = LogFactory.getLog(MetricsService.class);

    @Autowired
    private CompositeMeterRegistry compositeMeterRegistry;

    @Autowired
    private DiskSpaceHealthIndicator diskSpaceHealthIndicator;

    public List<Pair<String, String>> getMetricsList() {

        List<Pair<String, String>> list = new ArrayList<Pair<String, String>>();

        addHealthInfo("DiskSpace", list, diskSpaceHealthIndicator.health());

        addMeterInfo(list, compositeMeterRegistry.getMeters());

        Collections.sort(list, new Comparator<Pair<String, String>>() {
            @Override
            public int compare(Pair<String, String> obj1, Pair<String, String> obj2) {
                return obj1.getKey().compareTo(obj2.getKey());
            }
        });

        return list;
    }

    protected void addMeterInfo(List<Pair<String, String>> list, Collection<Meter> meteres) {
        Iterator<Meter> iterator = meteres.iterator();
        while (iterator.hasNext()) {
            Meter meter = (Meter) iterator.next();
            Id meterId = meter.getId();
            StringBuilder sb = new StringBuilder();
            Iterator<Measurement> ite2 = meter.measure().iterator();
            while (ite2.hasNext()) {
                Measurement measurement = (Measurement) ite2.next();
                sb.append(measurement.getValue());
                if (ite2.hasNext()) {
                    sb.append(",");
                }
            }
            list.add(new MutablePair<String, String>(meterId2String(meterId), sb.toString()));
        }
    }

    protected String meterId2String(Id meterId) {
        StringBuilder sb = new StringBuilder();
        if (meterId.getTags() != null) {
            Iterator<Tag> iterator = meterId.getTags().iterator();
            while (iterator.hasNext()) {
                Tag tag = iterator.next();
                String tagkey = tag.getKey();
                if (StringUtils.equals(tagkey, "id")) {
                    sb.insert(0, ",");
                    sb.insert(0, tag.getValue());
                }
                else if (StringUtils.equals(tagkey, "area") == false) {
                    sb.append(tag.getKey()).append("=").append(tag.getValue()).append(",");
                }
            }
        }

        if (sb.length() != 0) {
            sb.delete(sb.length() - 1, sb.length()); // remove last comma
            sb.insert(0, "[");
            sb.insert(0, meterId.getName());
            sb.append("]");
        }
        else {
            sb.append(meterId.getName());
        }

        return sb.toString();
    }

    protected void addHealthInfo(String prefix, List<Pair<String, String>> list, Health health) {
        Map<String, Object> map = health.getDetails();
        Iterator<String> ite1 = map.keySet().iterator();
        while (ite1.hasNext()) {
            String key = ite1.next();
            Object object = map.get(key);
            list.add(new MutablePair<String, String>(prefix + "." + key, object.toString()));
        }
    }
}
