package net.afnf.blog.thymeleaf;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import net.afnf.blog.thymeleaf.processor.CommentProcessor;
import net.afnf.blog.thymeleaf.processor.DateProcessor;
import net.afnf.blog.thymeleaf.processor.IfProcessor;
import net.afnf.blog.thymeleaf.processor.TimeProcessor;
import net.afnf.blog.thymeleaf.processor.TitleProcessor;

public class MyFunctionDialect extends AbstractProcessorDialect {

    public static final String PREFIX = "f";

    public MyFunctionDialect() {
        super("MyFunctionDialect", PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
    }

    @Override
    public Set<IProcessor> getProcessors(final String dialectPrefix) {
        final Set<IProcessor> processors = new HashSet<IProcessor>();
        processors.add(new TimeProcessor());
        processors.add(new DateProcessor());
        processors.add(new TitleProcessor());
        processors.add(new CommentProcessor());
        processors.add(new IfProcessor());
        return processors;
    }
}

