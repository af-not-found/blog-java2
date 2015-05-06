package net.afnf.blog.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.afnf.blog.bean.EntryCache;
import net.afnf.blog.bean.EntryCondition;
import net.afnf.blog.bean.EntryList;
import net.afnf.blog.bean.NameCountPair;
import net.afnf.blog.common.AfnfUtil;
import net.afnf.blog.common.IfModifiedSinceFilter;
import net.afnf.blog.domain.Entry;
import net.afnf.blog.mapper.EntryMapperCustomized;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntryService {

    public enum EntryState {
        DRAFT, NORMAL, DELETE
    }

    final public static int LIMIT = 30;
    final public static int RECENTS = 8;

    @Autowired
    private EntryMapperCustomized em;

    public Entry getEntry(Integer id) {
        return em.selectByPrimaryKey(id);
    }

    public EntryList getEntriesByTag(String tag, Integer page) {
        return getEntries(tag, null, page, true);
    }

    public EntryList getEntriesByMonth(String month) {
        return getEntries(null, month, 1, true);
    }

    public EntryList getEntriesWithDeleted(Integer page) {
        return getEntries(null, null, page, false);
    }

    protected EntryList getEntries(String tag, String month, Integer page, boolean filter) {

        int pageInt = 1, offset = 0, limit = LIMIT;
        if (page != null) {
            limit = LIMIT;
            pageInt = page.intValue();
            offset = Math.max(0, pageInt - 1) * limit;
        }
        // 月別ページはページング無し
        if (StringUtils.isNotBlank(month)) {
            limit = -1;
        }

        EntryCondition cond = new EntryCondition(tag, month, limit, offset, filter);
        EntryList ret = new EntryList(limit);
        ret.setEntries(em.selectWithCond(cond));
        ret.setTotalCount(em.countWithCond(cond));
        ret.setThisPage(pageInt);

        return ret;
    }

    public void insertOrUpdate(Integer id, Entry newEntry, boolean updatePostdate) {

        // タグの正規化
        newEntry.setTags(AfnfUtil.normalizegTag(newEntry.getTags()));

        // 編集
        if (id >= 0) {
            newEntry.setId(id);
            if (updatePostdate) {
                newEntry.setPostdate(new Date(System.currentTimeMillis()));
            }
            em.updateByPrimaryKeySelective(newEntry);
        }
        // 新規
        else {
            em.insertSelective(newEntry);
        }
    }

    public void updateEntryCache() {
        EntryCache cache = EntryCache.getInstance();
        updateEntryCache(cache, true);
    }

    public EntryCache getEntryCache() {
        EntryCache cache = EntryCache.getInstance();
        if (cache.getUpdated() == 0) {
            updateEntryCache(cache, true);
        }
        return cache;
    }

    private void updateEntryCache(EntryCache cache, boolean force) {
        cache.setTotalNormalCount(getTotalNormalCount());
        cache.setMonthlyList(getMonthlyList());
        cache.setTagList(getTagList());
        List<Entry> entries = getEntriesByTag(null, null).getEntries();
        cache.setRecents(entries.subList(0, Math.min(entries.size(), RECENTS)));
        cache.setUpdated(System.currentTimeMillis());
        IfModifiedSinceFilter.updateLastModified();
    }

    protected int getTotalNormalCount() {
        EntryCondition cond = new EntryCondition(null, null, 0, 0, true);
        return em.countWithCond(cond);
    }

    protected List<NameCountPair> getMonthlyList() {
        return em.selectMonthlyCount();
    }

    protected List<NameCountPair> getTagList() {
        List<NameCountPair> tagList = new ArrayList<NameCountPair>();
        Map<String, Integer> tagMap = new HashMap<String, Integer>();

        List<NameCountPair> pairList = em.selectTagCount();
        for (NameCountPair pair : pairList) {
            List<String> tags = AfnfUtil.getTagList(pair.getKey());
            for (String tag : tags) {
                Integer count = tagMap.get(tag);
                int thisCount = pair.getCount();
                thisCount += (count == null ? 0 : count);
                tagMap.put(tag, thisCount);
            }
        }

        Iterator<String> ite = tagMap.keySet().iterator();
        while (ite.hasNext()) {
            String tag = (String) ite.next();
            NameCountPair t = new NameCountPair(tag, tagMap.get(tag), tag);
            tagList.add(t);
        }

        Collections.sort(tagList, new Comparator<NameCountPair>() {
            public int compare(NameCountPair o1, NameCountPair o2) {
                int c = o1.getCount() - o2.getCount();
                if (c == 0) {
                    return o1.getDisp().compareTo(o2.getDisp());
                }
                else {
                    return -c;
                }
            }
        });

        return tagList;
    }
}
