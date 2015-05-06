package net.afnf.blog.common;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AfnfUtil {

    private static Logger logger = LoggerFactory.getLogger(AfnfUtil.class);

    public static List<String> getTagList(String tags) {

        ArrayList<String> list = new ArrayList<String>();

        if (StringUtils.isNotBlank(tags)) {
            String[] values = StringUtils.split(tags, ",");
            Set<String> set = new LinkedHashSet<String>();
            for (int j = 0; j < values.length; j++) {
                String tag = StringUtils.strip(values[j], " \u3000");
                if (StringUtils.isNotBlank(tag) && set.contains(tag) == false) {
                    set.add(tag);
                }
            }
            list.addAll(set);
        }

        return list;
    }

    public static String normalizegTag(String tags) {
        List<String> list = getTagList(tags);
        return StringUtils.join(list, ", ");
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        }
        catch (Exception e) {
            logger.warn(e.toString());
        }
    }
}
