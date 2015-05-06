package net.afnf.blog.thymeleaf;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.afnf.blog.common.AssetsFunction;
import net.afnf.blog.common.MyFunction;
import net.afnf.blog.thymeleaf.processor.ProcessorUtil;
import net.afnf.blog.thymeleaf.processor.TextProcessor;
import net.afnf.blog.thymeleaf.processor.UTextProcessor;

import org.thymeleaf.Arguments;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionEnhancingDialect;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.IProcessor;

public class MyFunctionDialect extends AbstractDialect implements IExpressionEnhancingDialect {

    public MyFunctionDialect() {
        super();
    }

    @Override
    public String getPrefix() {
        return "f";
    }

    @Override
    public Set<IProcessor> getProcessors() {
        final Set<IProcessor> processors = new HashSet<IProcessor>();
        processors.add(new TimeProcessor("time"));
        processors.add(new DateProcessor("date"));
        processors.add(new TitleProcessor("title"));
        processors.add(new CommentProcessor("comment"));
        return processors;
    }

    @Override
    public Map<String, Object> getAdditionalExpressionObjects(final IProcessingContext processingContext) {
        final Map<String, Object> additionalExpressionObjects = new HashMap<String, Object>(2, 1);
        additionalExpressionObjects.put("f", AssetsFunction.getInstance());
        return additionalExpressionObjects;
    }
}

class TimeProcessor extends TextProcessor {

    public TimeProcessor(String attrName) {
        super(attrName);
    }

    protected String process(Object result) {
        return MyFunction.getInstance().formatLongTime((Long) result);
    }
}

class DateProcessor extends TextProcessor {

    public DateProcessor(String attrName) {
        super(attrName);
    }

    protected String process(Object result) {
        return MyFunction.getInstance().formatDate((Date) result);
    }
}

class TitleProcessor extends TextProcessor {

    public TitleProcessor(String attrName) {
        super(attrName);
    }

    @Override
    protected String getText(final Arguments arguments, final Element element, final String attributeName) {
        String str = null;

        Object entry = ProcessorUtil.parse(arguments, "${entry}", true);
        Object entryTitle = null;
        if (entry != null) {
            entryTitle = ProcessorUtil.parse(arguments, "${entry.title}", true);
            if (entryTitle != null) {
                str = (String) entryTitle;
            }
        }

        if (str == null) {
            Object currentTag = ProcessorUtil.parse(arguments, "${currentTag}", true);
            if (currentTag != null) {
                str = (String) currentTag;
            }
        }

        if (str == null) {
            Object currentMonth = ProcessorUtil.parse(arguments, "${currentMonth}", true);
            if (currentMonth != null) {
                str = (String) currentMonth;
            }
        }

        return MyFunction.getInstance().generateTitle(str);
    }

    @Override
    protected String process(Object result) {
        return null;
    }
}

class CommentProcessor extends UTextProcessor {

    public CommentProcessor(String attrName) {
        super(attrName);
    }

    protected String process(Object result) {
        return MyFunction.getInstance().renderComment((String) result);
    }
}