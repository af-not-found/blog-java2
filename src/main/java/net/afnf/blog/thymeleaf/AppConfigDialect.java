package net.afnf.blog.thymeleaf;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

import net.afnf.blog.config.AppConfig;

public class AppConfigDialect implements IExpressionObjectDialect {

    private static final String EXPRESSION_OBJECT_NAME = "conf";

    @SuppressWarnings("serial")
    private static final Set<String> ALL_EXPRESSION_NAMES = new HashSet<String>() {
        {
            add(EXPRESSION_OBJECT_NAME);
        }
    };

    @Override
    public String getName() {
        return "AppConfig";
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return new AppConfigExpressionObjectFactory();
    }

    public static class AppConfigExpressionObjectFactory implements IExpressionObjectFactory {

        @Override
        public boolean isCacheable(String expressionObjectName) {
            return true;
        }

        @Override
        public Set<String> getAllExpressionObjectNames() {
            return ALL_EXPRESSION_NAMES;
        }

        @Override
        public Object buildObject(IExpressionContext context, String expressionObjectName) {
            if (EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
                return AppConfig.getInstance();
            }
            return null;
        }

    }
}