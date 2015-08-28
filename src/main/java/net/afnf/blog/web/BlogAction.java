package net.afnf.blog.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.afnf.blog.bean.EntryList;
import net.afnf.blog.bean.NameCountPair;
import net.afnf.blog.common.SmtpManager;
import net.afnf.blog.config.AppConfig;
import net.afnf.blog.domain.Comment;
import net.afnf.blog.domain.Entry;
import net.afnf.blog.service.CommentService;
import net.afnf.blog.service.CommentService.CommentState;
import net.afnf.blog.service.EntryService;
import net.afnf.blog.service.EntryService.EntryState;

@Controller
public class BlogAction extends TokenCheckableAction {

    private static Logger logger = LoggerFactory.getLogger(BlogAction.class);

    @Autowired
    private EntryService es;
    @Autowired
    private CommentService cs;
    @Autowired
    private SmtpManager smtpManager;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(@RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "tag", required = false) String tag, Model model) {
        return tag(null, page, model);
    }

    @RequestMapping(value = "/t/{tag}", method = RequestMethod.GET)
    public String tag(@PathVariable String tag, @RequestParam(value = "page", required = false) Integer page, Model model) {

        // tagListにない場合はtopへ
        if (StringUtils.isNotBlank(tag)) {
            boolean found = false;
            List<NameCountPair> tagList = es.getEntryCache().getTagList();
            if (tagList != null) {
                for (NameCountPair tagInfo : tagList) {
                    if (StringUtils.equalsIgnoreCase(tag, tagInfo.getKey())) {
                        found = true;
                    }
                }
            }
            if (found == false) {
                logger.info("invalid tag : " + tag);
                return "redirect:/";
            }
        }

        model.addAttribute("currentTag", tag);
        model.addAttribute("pagingList", es.getEntriesByTag(tag, page));

        setCommonAttribute(model, true);
        return "blog";
    }

    @RequestMapping(value = "/m/{month}", method = RequestMethod.GET)
    public String month(@PathVariable String month, Model model) {

        // パースに失敗した場合はtopへ
        String monthDisp = "";
        try {
            SimpleDateFormat sdf_param = new SimpleDateFormat("yyyyMM");
            Date date = sdf_param.parse(month);
            SimpleDateFormat sdf_disp = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
            monthDisp = sdf_disp.format(date);
        }
        catch (Throwable e) {
            return "redirect:/";
        }

        model.addAttribute("currentMonth", monthDisp);
        EntryList entryList = es.getEntriesByMonth(month);
        entryList.setLimit(-1);
        model.addAttribute("pagingList", entryList);

        NameCountPair[] prevNextMonths = getPrevNextMonth(month, es.getEntryCache().getMonthlyList());
        model.addAttribute("prevMonth", prevNextMonths[0]);
        model.addAttribute("nextMonth", prevNextMonths[1]);

        setCommonAttribute(model, true);
        return "blog";
    }

    @RequestMapping(value = "/{entryid:[0-9]+}", method = RequestMethod.GET)
    public String single(@PathVariable Integer entryid, Model model) {

        Entry entry = es.getEntry(entryid);

        if (entry != null && entry.getState() == EntryState.NORMAL.ordinal()) {
            List<Comment> comments = cs.getEntryComments(entryid);
            model.addAttribute("comments", comments);
            entry.setCommentCount(comments.size());

            model.addAttribute("entry", entry);
            model.addAttribute("comment", new Comment());
        }
        else {
            return "redirect:/";
        }

        setCommonAttribute(model, false);
        return "blog";
    }

    @RequestMapping(value = "/{entryid:[0-9]+}", params = "post", method = RequestMethod.POST)
    @ResponseBody
    public String postComment(@PathVariable Integer entryid, @ModelAttribute("comment") @Valid Comment comment) {
        checkToken();

        Comment newComment = new Comment();
        newComment.setEntryid(entryid);
        newComment.setName(comment.getName());
        newComment.setContent(comment.getContent());
        newComment.setClientinfo(getClientInfo());
        newComment.setState((short) CommentState.WAIT.ordinal());
        cs.registerComment(newComment);

        StringBuilder sb = new StringBuilder();
        sb.append(comment.getName());
        sb.append("\n");
        sb.append(comment.getContent());
        sb.append("\n\n");
        sb.append(AppConfig.getInstance().getAdminHost());
        sb.append(request.getServletContext().getContextPath());
        sb.append("/_admin/comments");
        smtpManager.send("new blog comment", sb.toString());

        return SUCCESS_JSON;
    }

    protected void setCommonAttribute(Model model, boolean isSummaryPage) {
        model.addAttribute("isAdminPage", false);
        model.addAttribute("isSummaryPage", isSummaryPage);
        model.addAttribute("isProductionAndNormalSite", AppConfig.getInstance().isProductionAndNormalSite());
        model.addAttribute("isTestSite", AppConfig.getInstance().isTestSite());
        model.addAttribute("entryCache", es.getEntryCache());
    }

    protected static NameCountPair[] getPrevNextMonth(String month, List<NameCountPair> monthlyList) {

        NameCountPair[] prevNextMonths = new NameCountPair[2];
        int index = Integer.MIN_VALUE;
        int size = monthlyList != null ? monthlyList.size() : 0;
        for (int i = 0; i < size; i++) {
            NameCountPair pair = monthlyList.get(i);
            if (StringUtils.equals(month, pair.getKey())) {
                index = i;
                break;
            }
        }

        int prexIndex = index + 1;
        if (0 <= prexIndex && prexIndex < size) {
            prevNextMonths[0] = monthlyList.get(prexIndex);
        }

        int nextIndex = index - 1;
        if (0 <= nextIndex && nextIndex < size) {
            prevNextMonths[1] = monthlyList.get(nextIndex);
        }

        return prevNextMonths;
    }
}
