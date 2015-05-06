package net.afnf.blog.thymeleaf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.afnf.blog.config.AppConfig;

import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionEnhancingDialect;
import org.thymeleaf.processor.IProcessor;

public class AppConfigDialect extends AbstractDialect implements IExpressionEnhancingDialect {

    public AppConfigDialect() {
        super();
    }

    @Override
    public String getPrefix() {
        return "conf";
    }

    @Override
    public Set<IProcessor> getProcessors() {
        final Set<IProcessor> processors = new HashSet<IProcessor>(2, 1);
        return processors;
    }

    @Override
    public Map<String, Object> getAdditionalExpressionObjects(final IProcessingContext processingContext) {
        final Map<String, Object> additionalExpressionObjects = new HashMap<String, Object>(2, 1.0f);
        additionalExpressionObjects.put("conf", AppConfig.getInstance());
        return additionalExpressionObjects;
    }
}