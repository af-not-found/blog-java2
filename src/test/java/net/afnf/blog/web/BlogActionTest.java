package net.afnf.blog.web;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import net.afnf.blog.SpringTestBase;
import net.afnf.blog.bean.NameCountPair;

import org.junit.Test;

public class BlogActionTest extends SpringTestBase {

    @Test
    public void testGetPrevNextMonth1() {
        assertEquals("null,null", exec(null, null));
        assertEquals("null,null", exec("201301", null));

        List<NameCountPair> monthlyList = new ArrayList<NameCountPair>();
        assertEquals("null,null", exec(null, monthlyList));
        assertEquals("null,null", exec("201301", monthlyList));

        monthlyList.add(new NameCountPair("201302", 1, "201302d"));
        assertEquals("null,null", exec(null, monthlyList));
        assertEquals("null,null", exec("201301", monthlyList));

        monthlyList.add(new NameCountPair("201301", 1, "201301d"));
        assertEquals("null,null", exec(null, monthlyList));
        assertEquals("null,201302d", exec("201301", monthlyList));

        monthlyList.add(new NameCountPair("100102", 1, "100102d"));
        assertEquals("100102d,201302d", exec("201301", monthlyList));
    }

    @Test
    public void testGetPrevNextMonth2() {

        List<NameCountPair> monthlyList = new ArrayList<NameCountPair>();
        monthlyList.add(new NameCountPair("100102", 1, "100102d"));
        assertEquals("null,null", exec("100102", monthlyList));

        monthlyList.add(new NameCountPair("100101", 1, "100101d"));
        assertEquals("100101d,null", exec("100102", monthlyList));
    }

    @Test
    public void testGetPrevNextMonth3() {

        List<NameCountPair> monthlyList = new ArrayList<NameCountPair>();

        monthlyList.add(new NameCountPair("201304", 1, "201304d"));
        monthlyList.add(new NameCountPair("201303", 1, "201303d"));
        monthlyList.add(new NameCountPair("201302", 1, "201302d"));
        monthlyList.add(new NameCountPair("201301", 1, "201301d"));

        assertEquals("null,201302d", exec("201301", monthlyList));
        assertEquals("201301d,201303d", exec("201302", monthlyList));
        assertEquals("201302d,201304d", exec("201303", monthlyList));
        assertEquals("201303d,null", exec("201304", monthlyList));
    }

    @Test
    public void testGetPrevNextMonth4() {

        List<NameCountPair> monthlyList = new ArrayList<NameCountPair>();

        monthlyList.add(new NameCountPair("201307", 1, "201307d"));
        monthlyList.add(new NameCountPair("201305", 1, "201305d"));
        monthlyList.add(new NameCountPair("201303", 1, "201303d"));
        monthlyList.add(new NameCountPair("201301", 1, "201301d"));

        assertEquals("null,201303d", exec("201301", monthlyList));
        assertEquals("null,null", exec("201302", monthlyList));
        assertEquals("201301d,201305d", exec("201303", monthlyList));
        assertEquals("null,null", exec("201304", monthlyList));
        assertEquals("201303d,201307d", exec("201305", monthlyList));
        assertEquals("null,null", exec("201306", monthlyList));
        assertEquals("201305d,null", exec("201307", monthlyList));
    }

    protected String exec(String month, List<NameCountPair> monthlyList) {
        NameCountPair[] p = BlogAction.getPrevNextMonth(month, monthlyList);
        return (p[0] == null ? null : p[0].getDisp()) + "," + (p[1] == null ? null : p[1].getDisp());
    }
}
