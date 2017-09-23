package net.afnf.blog.thymeleaf.processor;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.WebEngineContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.processor.AbstractStandardConditionalVisibilityTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EvaluationUtils;

import net.afnf.blog.thymeleaf.MyFunctionDialect;

/**
 * @See StandardIfTagProcessor
 */
public class IfProcessor extends AbstractStandardConditionalVisibilityTagProcessor {

    public static final int PRECEDENCE = 300;

    public IfProcessor() {
        super(TemplateMode.HTML, MyFunctionDialect.PREFIX, "if", PRECEDENCE);
    }

    @Override
    protected boolean isVisible(final ITemplateContext context, final IProcessableElementTag tag,
            final AttributeName attributeName, final String attributeValue) {

        String attributeValueTmp = attributeValue;
        if (StringUtils.indexOf(attributeValueTmp, "isAdminPage") != -1) {
            if (context instanceof WebEngineContext) {
                WebEngineContext webContext = (WebEngineContext) context;
                HttpServletRequest httpServletRequest = webContext.getRequest();
                boolean isAdminPage = StringUtils.indexOf(httpServletRequest.getRequestURI(), "/_admin/") != -1;
                attributeValueTmp = StringUtils.replace(attributeValueTmp, "isAdminPage", Boolean.toString(isAdminPage));
            }
        }

        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());
        final IStandardExpression expression = expressionParser.parseExpression(context, attributeValueTmp);
        final Object value = expression.execute(context);

        return EvaluationUtils.evaluateAsBoolean(value);
    }
}
