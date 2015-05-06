package net.afnf.blog.thymeleaf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.afnf.blog.common.AssetsFunction;
import net.afnf.blog.thymeleaf.processor.AttrProcessor;

import org.thymeleaf.Arguments;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionEnhancingDialect;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.IProcessor;

public class AssetsDialect extends AbstractDialect implements IExpressionEnhancingDialect {

    public AssetsDialect() {
        super();
    }

    @Override
    public String getPrefix() {
        return "as";
    }

    @Override
    public Set<IProcessor> getProcessors() {
        final Set<IProcessor> processors = new HashSet<IProcessor>();
        processors.add(new AssetsUrlAttrProcessor("src"));
        processors.add(new AssetsUrlAttrProcessor("href"));
        return processors;
    }

    @Override
    public Map<String, Object> getAdditionalExpressionObjects(final IProcessingContext processingContext) {
        final Map<String, Object> additionalExpressionObjects = new HashMap<String, Object>(2, 1);
        additionalExpressionObjects.put("as", AssetsFunction.getInstance());
        return additionalExpressionObjects;
    }
}

class AssetsUrlAttrProcessor extends AttrProcessor {

    public AssetsUrlAttrProcessor(String attrName) {
        super(attrName);
    }

    @Override
    protected String getTargetAttributeValue(final Arguments arguments, final Element element, final String attributeName) {
        final String attributeValue = element.getAttributeValue(attributeName);
        return AssetsFunction.getInstance().url(attributeValue);
    }
}