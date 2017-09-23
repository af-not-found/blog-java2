package net.afnf.blog.thymeleaf.processor;

import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.NoOpToken;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;

public class ProcessorUtil {

    /**
     * @See AbstractStandardExpressionAttributeTagProcessor
     */
    public static Object parse(final ITemplateContext context, final String attributeValue, boolean escape) {

        IEngineConfiguration configuration = context.getConfiguration();

        IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);

        IStandardExpression expression = parser.parseExpression(context, attributeValue);

        Object expressionResult = null;
        if (attributeValue != null) {

            if (expression != null && expression instanceof FragmentExpression) {

                final FragmentExpression.ExecutedFragmentExpression executedFragmentExpression = FragmentExpression
                        .createExecutedFragmentExpression(context, (FragmentExpression) expression,
                                StandardExpressionExecutionContext.NORMAL);

                expressionResult = FragmentExpression.resolveExecutedFragmentExpression(context, executedFragmentExpression,
                        true);
            }
            else {
                expressionResult = expression.execute(context);
            }
        }

        if (expressionResult != null && expressionResult != NoOpToken.VALUE) {
            if (escape && expressionResult instanceof String) {
                expressionResult = StringEscapeUtils.escapeXml11((String) expressionResult);
            }
        }

        return expressionResult;
    }
}
