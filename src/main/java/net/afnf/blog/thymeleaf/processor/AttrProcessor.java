package net.afnf.blog.thymeleaf.processor;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.standard.processor.attr.AbstractStandardSingleAttributeModifierAttrProcessor;

public abstract class AttrProcessor extends AbstractStandardSingleAttributeModifierAttrProcessor {

    public static final int ATTR_PRECEDENCE = 1000;
    private String attrName = null;

    public AttrProcessor(String attrName) {
        super(attrName);
        this.attrName = attrName;
    }

    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }

    @Override
    protected String getTargetAttributeName(final Arguments arguments, final Element element, final String attributeName) {
        return this.attrName;
    }

    abstract protected String getTargetAttributeValue(final Arguments arguments, final Element element, final String attributeName);

    @Override
    protected ModificationType getModificationType(final Arguments arguments, final Element element, final String attributeName,
            final String newAttributeName) {
        return ModificationType.SUBSTITUTION;
    }

    @Override
    protected boolean removeAttributeIfEmpty(final Arguments arguments, final Element element, final String attributeName,
            final String newAttributeName) {
        return false;
    }
}
