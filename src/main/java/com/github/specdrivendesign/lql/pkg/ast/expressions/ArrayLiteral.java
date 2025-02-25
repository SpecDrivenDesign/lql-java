package com.github.specdrivendesign.lql.pkg.ast.expressions;

import com.github.specdrivendesign.lql.pkg.ast.Expression;
import com.github.specdrivendesign.lql.pkg.env.Env;
import java.util.ArrayList;
import java.util.List;

// ArrayLiteral represents an array literal expression.
public class ArrayLiteral implements Expression {
    private List<Expression> elements;
    private int line;
    private int column;

    public ArrayLiteral(List<Expression> elements, int line, int column) {
        this.elements = elements;
        this.line = line;
        this.column = column;
    }

    @Override
    public Object eval(java.util.Map<String, Object> ctx, Env env) throws Exception {
        List<Object> result = new ArrayList<>();
        for (Expression expr : elements) {
            result.add(expr.eval(ctx, env));
        }
        return result;
    }

    @Override
    public int[] pos() {
        return new int[]{line, column};
    }

    @Override
    public String toString() {
        String openBracket = "[";
        String closeBracket = "]";
        String comma = ", ";
        if (Color.isEnabled()) {
            openBracket = Color.getPunctuationColor() + "[" + Color.getReset();
            closeBracket = Color.getPunctuationColor() + "]" + Color.getReset();
            comma = Color.getPunctuationColor() + "," + Color.getReset() + " ";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(openBracket);
        for (int i = 0; i < elements.size(); i++) {
            if (i > 0) {
                sb.append(comma);
            }
            sb.append(elements.get(i).toString());
        }
        sb.append(closeBracket);
        return sb.toString();
    }
}
