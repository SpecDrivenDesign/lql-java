// --------- FILE START: "Identifier.java" (converted from pkg/ast/expressions/identifier.go) ----------
package com.github.ryancopley.lql.pkg.ast.expressions;

import com.github.ryancopley.lql.pkg.env.Env;
import com.github.ryancopley.lql.pkg.errors.Errors;
import com.github.ryancopley.lql.pkg.ast.Expression;

import java.util.Map;

public class Identifier implements Expression {
    private String name;
    private int line;
    private int column;

    public Identifier(String name, int line, int column) {
        this.name = name;
        this.line = line;
        this.column = column;
    }

    public String getName() {
        return name;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public Object eval(Map<String, Object> ctx, Env env) throws Exception {
        // Bare identifiers are not allowed outside context references.
        throw Errors.newUnknownIdentifierError("Bare identifier '" + name + "' is not allowed", line, column);
    }

    @Override
    public int[] pos() {
        return new int[]{line, column};
    }

    @Override
    public String toString() {
        if (Color.isEnabled()) {
            return Color.getIdentifierColor() + name + Color.getReset();
        }
        return name;
    }
}
// --------- FILE END: "Identifier.java" ----------
