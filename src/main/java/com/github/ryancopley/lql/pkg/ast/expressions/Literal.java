// --------- FILE START: "Literal.java" (converted from pkg/ast/expressions/literal.go) ----------
package com.github.ryancopley.lql.pkg.ast.expressions;

import com.github.ryancopley.lql.pkg.env.Env;
import com.github.ryancopley.lql.pkg.ast.Expression;

import java.util.Map;

public class Literal implements Expression {
    private Object value;
    private int line;
    private int column;

    public Literal(Object value, int line, int column) {
        this.value = value;
        this.line = line;
        this.column = column;
    }

    @Override
    public Object eval(Map<String, Object> ctx, Env env) throws Exception {
        return value;
    }

    @Override
    public int[] pos() {
        return new int[]{line, column};
    }

    @Override
    public String toString() {
        String s;
        if (value instanceof String) {
            s = "\"" + value + "\"";
            if (Color.isEnabled()) {
                s = Color.getStringColor() + s + Color.getReset();
            }
        } else if (value instanceof Boolean) {
            s = ((Boolean) value) ? "true" : "false";
            if (Color.isEnabled()) {
                s = Color.getBoolNullColor() + s + Color.getReset();
            }
        } else if (value == null) {
            s = "null";
            if (Color.isEnabled()) {
                s = Color.getBoolNullColor() + s + Color.getReset();
            }
        } else if (value instanceof Integer || value instanceof Long || value instanceof Double) {
            s = String.valueOf(value);
            if (Color.isEnabled()) {
                s = Color.getNumberColor() + s + Color.getReset();
            }
        } else {
            s = String.valueOf(value);
        }
        return s;
    }
}
// --------- FILE END: "Literal.java" ----------
