package net.afnf.blog.web.admin;

import javax.validation.Valid;

import net.afnf.blog.domain.Entry;
import net.afnf.blog.service.EntryService;
import net.afnf.blog.service.EntryService.EntryState;
import net.afnf.blog.web.TokenCheckableAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EntriesAction extends TokenCheckableAction {

    final static int LIMIT = 10;

    @Autowired
    private EntryService es;

    @RequestMapping(value = "/_admin", method = RequestMethod.GET)
    public String index() {
        return "redirect:/_admin/entries";
    }

    @RequestMapping(value = "/_admin/entries", method = RequestMethod.GET)
    public String index(@RequestParam(value = "page", required = false) Integer page, Model model) {

        model.addAttribute("pagingList", es.getEntriesWithDeleted(page));

        model.addAttribute("isAdminPage", true);
        model.addAttribute("isSummaryPage", true);

        return "_admin/entries";
    }

    @RequestMapping(value = "/_admin/entries/{entryid}", method = RequestMethod.GET)
    public String single(@PathVariable Integer entryid, Model model) {
        Entry entry = null;
        if (entryid >= 0) {
            entry = es.getEntry(entryid);
        }
        else {
            entry = new Entry();
            entry.setId(-1);
            entry.setState((short) EntryState.DRAFT.ordinal());
        }
        model.addAttribute("entry", entry);

        model.addAttribute("isAdminPage", true);
        model.addAttribute("isSummaryPage", false);

        return "_admin/entries";
    }

    @RequestMapping(value = "/_admin/entries/{entryid}", params = "post", method = RequestMethod.POST)
    @ResponseBody
    public String post(@ModelAttribute("entry") @Valid Entry entry) {
        checkToken();

        Integer id = entry.getId();
        Entry newEntry = new Entry();
        newEntry.setTitle(entry.getTitle());
        newEntry.setTags(entry.getTags());
        newEntry.setContent(entry.getContent());
        newEntry.setContentHtml(entry.getContentHtml());
        newEntry.setState(entry.getState());

        Entry prev = null;
        if (id != null && id.intValue() > 0) {
            prev = es.getEntry(id);
        }

        // 前回か今回がnormalの場合はキャッシュ更新
        boolean updateCache = false;
        short prevState = prev != null ? prev.getState().shortValue() : -1;
        if (prevState == EntryState.NORMAL.ordinal() || entry.getState() == EntryState.NORMAL.ordinal()) {
            updateCache = true;
        }

        // draft->normalの場合はpostdateを更新
        boolean updatePostdate = prevState == EntryState.DRAFT.ordinal() && entry.getState() == EntryState.NORMAL.ordinal();

        // 実行
        es.insertOrUpdate(id, newEntry, updatePostdate);

        // キャッシュ更新
        if (updateCache) {
            es.updateEntryCache();
        }

        // 採番されたIDを返す
        return "{\"id\":" + newEntry.getId() + "}";
    }
}
