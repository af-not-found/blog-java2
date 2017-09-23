package net.afnf.blog.thymeleaf.processor;

import net.afnf.blog.common.MyFunction;
import net.afnf.blog.thymeleaf.MyFunctionDialect;

public class CommentProcessor extends AbstractMyTextProcessor {

    public CommentProcessor() {
        super(MyFunctionDialect.PREFIX, "comment");
    }

    protected String processAndEscape(Object result) {
        // エスケープ処理はrenderComment内部で実施している
        return MyFunction.getInstance().renderComment((String) result);
    }
}