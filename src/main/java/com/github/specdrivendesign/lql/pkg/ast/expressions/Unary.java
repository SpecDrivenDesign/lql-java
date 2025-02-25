// --------- FILE START: "Unary.java" (converted from pkg/ast/expressions/unary.go) ----------
package com.github.specdrivendesign.lql.pkg.ast.expressions;

import com.github.specdrivendesign.lql.pkg.ast.Expression;
import com.github.specdrivendesign.lql.pkg.env.Env;
import com.github.specdrivendesign.lql.pkg.errors.Errors;
import com.github.specdrivendesign.lql.pkg.tokens.Tokens;
import com.github.specdrivendesign.lql.pkg.types.Types;

import java.util.Map;

public class Unary implements Expression {
    private int operator;
    private Expression expr;
    private int line;
    private int column;

    public Unary(int operator, Expression expr, int line, int column) {
        this.operator = operator;
        this.expr = expr;
        this.line = line;
        this.column = column;
    }

    @Override
    public Object eval(Map<String, Object> ctx, Env env) throws Exception {
        Object val = expr.eval(ctx, env);
        switch (operator) {
            case Tokens.TokenMinus: {
                Double num = Types.toFloat(val);
                if (num == null) {
                    throw Errors.newSemanticError("unary '-' operator requires a numeric operand", line, column);
                }
                if (Types.isInt(val)) {
                    return (long)(-num);
                }
                return -num;
            }
            case Tokens.TokenNot: {
                if (!(val instanceof Boolean)) {
                    throw Errors.newSemanticError("NOT operator requires a boolean operand", line, column);
                }
                return !((Boolean) val);
            }
            default:
                throw Errors.newUnknownOperatorError("unknown unary operator", line, column);
        }
    }

    @Override
    public int[] pos() {
        return new int[]{line, column};
    }

    @Override
    public String toString() {
        String exprStr = expr.toString();
        String opStr;
        switch (operator) {
            case Tokens.TokenMinus:
                opStr = "-";
                break;
            case Tokens.TokenNot:
                opStr = "NOT";
                break;
            default:
                opStr = Tokens.getFixedTokenLiteral(operator);
                break;
        }
        if (Color.isEnabled()) {
            opStr = Color.getOperatorColor() + opStr + Color.getReset();
        }
        if (operator == Tokens.TokenMinus) {
            return opStr + exprStr;
        }
        return opStr + " " + exprStr;
    }
}
// --------- FILE END: "Unary.java" ----------
