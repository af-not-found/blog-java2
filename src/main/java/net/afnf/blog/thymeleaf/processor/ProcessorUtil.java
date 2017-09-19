package net.afnf.blog.thymeleaf.processor;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.unbescape.html.HtmlEscape;

public class ProcessorUtil {

    public static Object parse(final ITemplateContext context, final String attributeValue, boolean escape) {

        IEngineConfiguration configuration = context.getConfiguration();

        IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);

        IStandardExpression expression = parser.parseExpression(context, attributeValue);

        Object result = (String) expression.execute(context);

        if (result instanceof String && escape) {
            result = HtmlEscape.escapeHtml5(result);
        }

        return result;
    }
}
