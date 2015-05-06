package net.afnf.blog.thymeleaf.processor;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractUnescapedTextChildModifierAttrProcessor;

public abstract class UTextProcessor extends AbstractUnescapedTextChildModifierAttrProcessor {

    public static final int ATTR_PRECEDENCE = 2000;

    public UTextProcessor(String attrName) {
        super(attrName);
    }

    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }

    @Override
    protected final String getText(final Arguments arguments, final Element element, final String attributeName) {

        final String attributeValue = element.getAttributeValue(attributeName);

        final Object result = ProcessorUtil.parse(arguments, attributeValue, false);

        return (result == null ? "" : process(result));
    }

    abstract protected String process(Object result);
}
