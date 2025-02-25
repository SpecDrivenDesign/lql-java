package com.github.specdrivendesign.lql.pkg.ast.expressions;

import com.github.specdrivendesign.lql.pkg.ast.Expression;
import com.github.specdrivendesign.lql.pkg.env.Env;
import com.github.specdrivendesign.lql.pkg.errors.Errors;

import java.util.Map;

public class Context implements Expression {
    private Identifier ident;
    private Expression subscript;
    private int line;
    private int column;

    public Context(Identifier ident, Expression subscript, int line, int column) {
        this.ident = ident;
        this.subscript = subscript;
        this.line = line;
        this.column = column;
    }

    @Override
    public Object eval(Map<String, Object> ctx, Env env) throws Exception {
        if (ident != null) {
            if (ctx.containsKey(ident.getName())) {
                return ctx.get(ident.getName());
            }
            throw Errors.newReferenceError(String.format("field '%s' not found", ident.getName()), ident.getLine(), ident.getColumn());
        }
        return ctx;
    }

    @Override
    public int[] pos() {
        return new int[]{line, column};
    }

    @Override
    public String toString() {
        String dollar = "$";
        if (Color.isEnabled()) {
            dollar = Color.getPunctuationColor() + "$" + Color.getReset();
        }
        if (ident != null) {
            String identName = ident.getName();
            if (Color.isEnabled()) {
                identName = Color.getContextColor() + identName + Color.getReset();
            }
            return dollar + identName;
        }
        if (subscript != null) {
            String openBracket = "[";
            String closeBracket = "]";
            if (Color.isEnabled()) {
                openBracket = Color.getPunctuationColor() + "[" + Color.getReset();
                closeBracket = Color.getPunctuationColor() + "]" + Color.getReset();
            }
            return dollar + openBracket + subscript.toString() + closeBracket;
        }
        return dollar;
    }
}
