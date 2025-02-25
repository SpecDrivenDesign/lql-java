package com.github.specdrivendesign.lql.pkg.ast;

import com.github.specdrivendesign.lql.pkg.env.Env;
import java.util.Map;

// Expression interface represents an AST node that can be evaluated.
public interface Expression {
    Object eval(Map<String, Object> ctx, Env env) throws Exception;
    int[] pos();
    @Override
    String toString();
}
