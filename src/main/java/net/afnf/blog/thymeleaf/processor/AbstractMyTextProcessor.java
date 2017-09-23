package net.afnf.blog.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

public abstract class AbstractMyTextProcessor extends AbstractStandardExpressionAttributeTagProcessor {

    private static final int PRECEDENCE = 12000;

    public AbstractMyTextProcessor(final String dialectPrefix, String attrName) {
        super(TemplateMode.HTML, dialectPrefix, attrName, PRECEDENCE, true);
    }

    abstract protected String processAndEscape(Object result);

    protected Object preProcess(final ITemplateContext context, final Object expressionResult) {
        return expressionResult;
    }

    @Override
    protected void doProcess(final ITemplateContext context, final IProcessableElementTag tag, final AttributeName attributeName,
            final String attributeValue, final Object expressionResult, final IElementTagStructureHandler structureHandler) {
        
        Object expressionResult2 = preProcess(context, expressionResult);

        String text = processAndEscape(expressionResult2);

        structureHandler.setBody(text, false);
    }
}
