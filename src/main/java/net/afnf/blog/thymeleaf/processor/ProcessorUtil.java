package net.afnf.blog.thymeleaf.processor;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;
import org.thymeleaf.standard.expression.StandardExpressions;

public class ProcessorUtil {

    public static Object parse(final Arguments arguments, final String attributeValue, boolean escape) {

        final Configuration configuration = arguments.getConfiguration();
        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);

        final IStandardExpression expression = expressionParser.parseExpression(configuration, arguments, attributeValue);

        final Object result;
        if (escape) {
            result = expression.execute(configuration, arguments);
        }
        else {
            result = expression.execute(configuration, arguments, StandardExpressionExecutionContext.UNESCAPED_EXPRESSION);
        }

        return result;
    }
}
