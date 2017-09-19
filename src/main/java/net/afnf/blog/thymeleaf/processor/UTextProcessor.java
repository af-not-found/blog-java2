package net.afnf.blog.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

public abstract class UTextProcessor extends AbstractAttributeTagProcessor {

    abstract protected String process(Object result);

    private static final int PRECEDENCE = 12000;

    public UTextProcessor(final String dialectPrefix, String attrName) {
        super(TemplateMode.HTML, // This processor will apply only to HTML mode
                dialectPrefix, // Prefix to be applied to name for matching
                null, // No tag name: match any tag name
                false, // No prefix to be applied to tag name
                attrName, // Name of the attribute that will be matched
                true, // Apply dialect prefix to attribute name
                PRECEDENCE, // Precedence (inside dialect's precedence)
                true); // Remove the matched attribute afterwards
    }

    @Override
    protected void doProcess(final ITemplateContext context, final IProcessableElementTag tag, final AttributeName attributeName,
            final String attributeValue, final IElementTagStructureHandler structureHandler) {
        Object result = ProcessorUtil.parse(context, attributeValue, false);
        String ret = process(result);
        structureHandler.setBody(ret, false);
    }
}
