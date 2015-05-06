package net.afnf.blog.web.admin;

import net.afnf.blog.service.EntryService;
import net.afnf.blog.web.TokenCheckableAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CacheAction extends TokenCheckableAction {

    @Autowired
    private EntryService es;

    @RequestMapping(value = "/_admin/cache", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("cache", es.getEntryCache());
        return "_admin/cache";
    }

    @RequestMapping(value = "/_admin/cache", method = RequestMethod.POST)
    @ResponseBody
    public String updateCache(RedirectAttributes attrs) {
        checkToken();

        long start = System.currentTimeMillis();
        es.updateEntryCache();
        long elapsed = System.currentTimeMillis() - start;

        return "{\"elapsed\":" + elapsed + "}";
    }
}
