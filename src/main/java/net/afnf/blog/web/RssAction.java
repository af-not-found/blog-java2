package net.afnf.blog.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.afnf.blog.common.MyFunction;
import net.afnf.blog.domain.Entry;
import net.afnf.blog.service.EntryService;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RssAction {

    @Autowired
    private EntryService es;

    @RequestMapping(value = "/rss.xml")
    public String rss(Model model, HttpServletResponse response) {
        StringBuilder sb = new StringBuilder();

        response.setContentType("application/rss+xml; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        List<Entry> entries = es.getEntriesByTag(null, 1).getEntries();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<rss version=\"2.0\"><channel><title>blog.afnf.net</title><link>http://blog.afnf.net/blog</link><language>ja</language>\n");

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

        try {
            ServletOutputStream os = response.getOutputStream();
            IOUtils.write(sb.toString(), os, "UTF-8");
            os.flush();
            IOUtils.closeQuietly(os);
        }
        catch (IOException e) {
            // do nothing
        }

        return null;
    }

}
