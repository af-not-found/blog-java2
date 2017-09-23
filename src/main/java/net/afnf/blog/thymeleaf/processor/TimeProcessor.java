package net.afnf.blog.thymeleaf.processor;

import net.afnf.blog.common.MyFunction;
import net.afnf.blog.thymeleaf.MyFunctionDialect;

public class TimeProcessor extends AbstractMyTextProcessor {

    public TimeProcessor() {
        super(MyFunctionDialect.PREFIX, "time");
    }

    protected String processAndEscape(Object result) {
        return MyFunction.getInstance().formatLongTime((Long) result);
    }
}
