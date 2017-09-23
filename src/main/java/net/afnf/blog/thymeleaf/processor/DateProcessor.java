package net.afnf.blog.thymeleaf.processor;

import java.util.Date;

import net.afnf.blog.common.MyFunction;
import net.afnf.blog.thymeleaf.MyFunctionDialect;

public class DateProcessor extends AbstractMyTextProcessor {

    public DateProcessor() {
        super(MyFunctionDialect.PREFIX, "date");
    }

    protected String processAndEscape(Object result) {
        return MyFunction.getInstance().formatDate((Date) result);
    }
}
