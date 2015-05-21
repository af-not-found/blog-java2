package net.afnf.blog.web.admin;

import net.afnf.blog.service.MetricsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MetricsAction {

    @Autowired
    private MetricsService ms;

    @RequestMapping(value = "/_admin/metrics", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("metricsList", ms.getMetricsList());
        return "_admin/metrics";
    }
}
