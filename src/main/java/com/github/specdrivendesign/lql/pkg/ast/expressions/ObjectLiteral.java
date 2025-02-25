// --------- FILE START: "ObjectLiteral.java" (converted from pkg/ast/expressions/objectliteral.go) ----------
package com.github.specdrivendesign.lql.pkg.ast.expressions;

import com.github.specdrivendesign.lql.pkg.ast.Expression;
import com.github.specdrivendesign.lql.pkg.env.Env;

import java.util.HashMap;
import java.util.Map;

public class ObjectLiteral implements Expression {
    private Map<String, Expression> fields;
    private int line;
    private int column;

    public ObjectLiteral(Map<String, Expression> fields, int line, int column) {
        this.fields = fields;
        this.line = line;
        this.column = column;
    }

    @Override
    public Object eval(Map<String, Object> ctx, Env env) throws Exception {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Expression> entry : fields.entrySet()) {
            result.put(entry.getKey(), entry.getValue().eval(ctx, env));
        }
        return result;
    }

    @Override
    public int[] pos() {
        return new int[]{line, column};
    }

    @Override
    public String toString() {
        String openBrace = "{";
        String closeBrace = "}";
        String colon = ": ";
        String comma = ", ";
        if (Color.isEnabled()) {
            openBrace = Color.getPunctuationColor() + "{" + Color.getReset();
            closeBrace = Color.getPunctuationColor() + "}" + Color.getReset();
            colon = Color.getPunctuationColor() + ":" + Color.getReset() + " ";
            comma = Color.getPunctuationColor() + "," + Color.getReset() + " ";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(openBrace);
        int i = 0;
        for (Map.Entry<String, Expression> entry : fields.entrySet()) {
            if (i > 0) {
                sb.append(comma);
            }
            String quotedKey = "\"" + entry.getKey() + "\"";
            if (Color.isEnabled()) {
                quotedKey = Color.getStringColor() + quotedKey + Color.getReset();
            }
            sb.append(quotedKey);
            sb.append(colon);
            sb.append(entry.getValue().toString());
            i++;
        }
        sb.append(closeBrace);
        return sb.toString();
    }
}
// --------- FILE END: "ObjectLiteral.java" ----------
