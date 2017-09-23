package net.afnf.blog.thymeleaf.processor;

import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.context.ITemplateContext;

import net.afnf.blog.common.MyFunction;
import net.afnf.blog.thymeleaf.MyFunctionDialect;

public class TitleProcessor extends AbstractMyTextProcessor {

    public TitleProcessor() {
        super(MyFunctionDialect.PREFIX, "title");
    }

    @Override
    protected Object preProcess(final ITemplateContext context, final Object expressionResult) {
        String str = null;

        Object entry = ProcessorUtil.parse(context, "${entry}", true);
        Object entryTitle = null;
        if (entry != null) {
            entryTitle = ProcessorUtil.parse(context, "${entry.title}", true);
            if (entryTitle != null) {
                str = (String) entryTitle;
            }
        }

        if (str == null) {
            Object currentTag = ProcessorUtil.parse(context, "${currentTag}", true);
            if (currentTag != null) {
                str = (String) currentTag;
            }
        }

        if (str == null) {
            Object currentMonth = ProcessorUtil.parse(context, "${currentMonth}", true);
            if (currentMonth != null) {
                str = (String) currentMonth;
            }
        }

        return MyFunction.getInstance().generateTitle(str);
    }

    @Override
    protected String processAndEscape(Object result) {
        return StringEscapeUtils.escapeXml11((String) result);
    }
}
