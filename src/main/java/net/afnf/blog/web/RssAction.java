package net.afnf.blog.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.afnf.blog.common.MyFunction;
import net.afnf.blog.domain.Entry;
import net.afnf.blog.service.EntryService;

@Controller
public class RssAction {

    @Autowired
    private EntryService es;

    @RequestMapping(value = "/rss.xml", produces = "application/rss+xml; charset=UTF-8")
    @ResponseBody
    public String rss(Model model) {
        StringBuilder sb = new StringBuilder();

        List<Entry> entries = es.getEntriesByTag(null, 1).getEntries();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<rss version=\"2.0\"><channel><title>blog.afnf.net</title>");
        sb.append("<link>http://blog.afnf.net/blog</link><language>ja</language>\n");

        for (Entry entry : entries) {
            sb.append("<item>");
            sb.append("<title>");
            sb.append(entry.getTitle());
            sb.append("</title>");
            sb.append("<link>http://blog.afnf.net/blog/");
            sb.append(entry.getId());
            sb.append("</link>");
            sb.append("<author>afnf</author>");
            sb.append("<pubDate>");
            sb.append(MyFunction.getInstance().formatPubDate(entry.getPostdate()));
            sb.append("</pubDate>");
            sb.append("</item>\n");
        }
        sb.append("</channel></rss>");

        return sb.toString();
    }
}
