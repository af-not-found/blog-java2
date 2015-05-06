package net.afnf.blog.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Date;

import net.afnf.blog.SpringTestBase;
import net.afnf.blog.bean.EntryList;
import net.afnf.blog.domain.Entry;
import net.afnf.blog.service.EntryService.EntryState;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EntryServiceTest extends SpringTestBase {

    @Autowired
    private EntryService es;

    @Test
    public void testInsertOrUpdate() {

        EntryList list;

        // チェック
        {
            list = es.getEntriesByTag(null, 1);
            assertEquals(0, list.getTotalCount());
        }

        // 1件目の登録
        int expectedId = 1;
        {
            Entry entry = new Entry();
            entry.setTitle("title" + expectedId);
            entry.setTags("tag1,tag3");
            entry.setContent("contentコンテンツ" + expectedId);
            entry.setContentHtml("content" + expectedId);
            entry.setState((short) EntryState.NORMAL.ordinal());
            es.insertOrUpdate(-1, entry, false);

            assertEquals(expectedId, entry.getId().intValue());
        }

        // チェック
        {
            list = es.getEntriesByTag(null, 1);
            assertEquals(1, list.getTotalCount());
            Entry entry = list.getEntries().get(0);
            assertEquals(expectedId, entry.getId().intValue());
            assertEquals("tag1, tag3", entry.getTags());
        }
        {
            list = es.getEntriesByTag("tag3", 1);
            assertEquals(1, list.getTotalCount());
        }
        {
            Entry entry = es.getEntry(expectedId);
            assertEquals("contentコンテンツ" + expectedId, entry.getContent());
        }

        // 2件目の登録
        expectedId = 2;
        executeSql("/sql/db-testdata-entry-1.sql");

        // チェック
        {
            list = es.getEntriesByTag(null, 1);
            assertEquals(2, list.getTotalCount());
            Entry entry = list.getEntries().get(0);
            assertEquals(expectedId, entry.getId().intValue());
            assertEquals("tag1, tag2, タグ3", entry.getTags());
        }
        {
            list = es.getEntriesByTag("tag1", 1);
            assertEquals(2, list.getTotalCount());
        }

        // 3件目の登録
        expectedId = 3;
        {
            Entry entry = new Entry();
            entry.setTitle("title" + expectedId);
            entry.setTags("tag1,tag4");
            entry.setContent("content" + expectedId);
            entry.setContentHtml("content" + expectedId);
            entry.setState((short) EntryState.NORMAL.ordinal());
            es.insertOrUpdate(-1, entry, false);

            assertEquals(expectedId, entry.getId().intValue());
        }

        // チェック
        {
            list = es.getEntriesByTag(null, 1);
            assertEquals(3, list.getTotalCount());
        }
        {
            list = es.getEntriesByTag("tag1", 1);
            assertEquals(3, list.getTotalCount());
        }
        {
            list = es.getEntriesByTag("tag4", 1);
            assertEquals(1, list.getTotalCount());
        }

        // 3件目を更新
        {
            Entry entry = new Entry();
            entry.setTitle("title" + expectedId);
            entry.setTags("tag1,tag5,タグ3");
            entry.setContent("content" + expectedId);
            entry.setContentHtml("content" + expectedId);
            entry.setState((short) EntryState.NORMAL.ordinal());
            es.insertOrUpdate(expectedId, entry, false);
        }

        // チェック
        {
            list = es.getEntriesByTag(null, 1);
            assertEquals(3, list.getTotalCount());
        }
        {
            list = es.getEntriesByTag("tag1", 1);
            assertEquals(3, list.getTotalCount());
        }
        {
            list = es.getEntriesByTag("tag4", 1);
            assertEquals(0, list.getTotalCount());
        }
        {
            list = es.getEntriesByTag("タグ3", 1);
            assertEquals(2, list.getTotalCount());
        }
        Date prevPostdate = null;
        {
            Entry entry = es.getEntry(expectedId);
            assertEquals("tag1, tag5, タグ3", entry.getTags());
            prevPostdate = entry.getPostdate();
        }

        // 3件目を更新
        {
            Entry entry = new Entry();
            entry.setTitle("title" + expectedId);
            entry.setTags("tag5,タグ3");
            entry.setContent("content" + expectedId);
            entry.setContentHtml("content" + expectedId);
            entry.setState((short) EntryState.DRAFT.ordinal());
            es.insertOrUpdate(expectedId, entry, false);
        }

        // チェック
        {
            list = es.getEntriesByTag(null, 1);
            assertEquals(2, list.getTotalCount());
        }
        {
            list = es.getEntriesByTag("tag1", 1);
            assertEquals(2, list.getTotalCount());
        }
        {
            list = es.getEntriesByTag("タグ3", 1);
            assertEquals(1, list.getTotalCount());
        }
        {
            Entry entry = es.getEntry(expectedId);
            assertEquals("tag5, タグ3", entry.getTags());
            assertEquals(prevPostdate, entry.getPostdate());
        }

        // 3件目を更新
        {
            Entry entry = new Entry();
            entry.setTitle("title" + expectedId);
            entry.setTags("tag5,タグ3");
            entry.setContent("コンテンツ" + expectedId);
            entry.setContentHtml("コンテンツ" + expectedId);
            entry.setState((short) EntryState.NORMAL.ordinal());
            es.insertOrUpdate(expectedId, entry, true);
        }

        // チェック
        {
            list = es.getEntriesByTag(null, 1);
            assertEquals(3, list.getTotalCount());
        }
        {
            list = es.getEntriesByTag("tag1", 1);
            assertEquals(2, list.getTotalCount());
        }
        {
            list = es.getEntriesByTag("タグ3", 1);
            assertEquals(2, list.getTotalCount());
        }
        {
            Entry entry = es.getEntry(expectedId);
            assertEquals("tag5, タグ3", entry.getTags());
            assertEquals("コンテンツ" + expectedId, entry.getContent());
            assertThat("check server date first", entry.getPostdate().getTime(), is(greaterThan(prevPostdate.getTime())));
        }

    }
}
