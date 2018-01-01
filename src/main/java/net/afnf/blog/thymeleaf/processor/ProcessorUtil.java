package net.afnf.blog.thymeleaf.processor;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
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

        final Object expressionResult;
        if (attributeValue != null) {

            if (expression != null && expression instanceof FragmentExpression) {
                // This is merely a FragmentExpression (not complex, not combined with anything), so we can apply a shortcut
                // so that we don't require a "null" result for this expression if the template does not exist. That will
                // save a call to resource.exists() which might be costly.

                final FragmentExpression.ExecutedFragmentExpression executedFragmentExpression = FragmentExpression
                        .createExecutedFragmentExpression(context, (FragmentExpression) expression);
                expressionResult = FragmentExpression.resolveExecutedFragmentExpression(context, executedFragmentExpression,
                        true);
            }
            else {

                /*
                 * Some attributes will require the execution of the expressions contained in them in RESTRICTED
                 * mode, so that e.g. access to request parameters is forbidden.
                 */
                final StandardExpressionExecutionContext expCtx = StandardExpressionExecutionContext.RESTRICTED;
                expressionResult = expression.execute(context, expCtx);
            }
        }
        else {
            expressionResult = null;
        }

        return expressionResult;
    }
}
