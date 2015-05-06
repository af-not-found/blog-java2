package net.afnf.blog.common;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class AfnfUtilTest {

    @Test
    public void testGetTagList() {

        int index = 0;
        List<String> tagList;

        tagList = AfnfUtil.getTagList(null);
        assertEquals(0, tagList.size());

        tagList = AfnfUtil.getTagList("");
        assertEquals(0, tagList.size());

        tagList = AfnfUtil.getTagList(" ");
        assertEquals(0, tagList.size());

        tagList = AfnfUtil.getTagList(",,");
        assertEquals(0, tagList.size());

        tagList = AfnfUtil.getTagList(" , ,  , ,,,, , ");
        assertEquals(0, tagList.size());

        tagList = AfnfUtil.getTagList("tag1,tag2,tag3");
        assertEquals(3, tagList.size());
        index = 0;
        assertEquals("tag1", tagList.get(index++));
        assertEquals("tag2", tagList.get(index++));
        assertEquals("tag3", tagList.get(index++));

        tagList = AfnfUtil.getTagList("tag1,,tag2,,tag3,tag3");
        assertEquals(3, tagList.size());
        index = 0;
        assertEquals("tag1", tagList.get(index++));
        assertEquals("tag2", tagList.get(index++));
        assertEquals("tag3", tagList.get(index++));

        tagList = AfnfUtil.getTagList(",,,,tag1,,tag2,,tag3,tag3  , , ,     ,, tag4 ,,  tag1,tag1,");
        assertEquals(4, tagList.size());
        index = 0;
        assertEquals("tag1", tagList.get(index++));
        assertEquals("tag2", tagList.get(index++));
        assertEquals("tag3", tagList.get(index++));
        assertEquals("tag4", tagList.get(index++));
    }

    @Test
    public void testNormalizegTag() {

        assertEquals("", AfnfUtil.normalizegTag(null));
        assertEquals("", AfnfUtil.normalizegTag(""));
        assertEquals("", AfnfUtil.normalizegTag(" "));
        assertEquals("", AfnfUtil.normalizegTag(" ,, "));
        assertEquals("", AfnfUtil.normalizegTag(" , ,  , ,,,, , "));
        assertEquals("tag1, tag2, tag3", AfnfUtil.normalizegTag("tag1,tag2,tag3"));
        assertEquals("tag1, tag2, tag3", AfnfUtil.normalizegTag("tag1,,tag2,,tag3,tag3"));
        assertEquals("tag1, tag2, tag3, tag4",
                AfnfUtil.normalizegTag(",,,,tag1,,tag2,,tag3,tag3  , , ,     ,, tag4 ,,  tag1,tag1,"));
    }
}
