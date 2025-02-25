package com.github.specdrivendesign.lql.pkg.ast.expressions;

import com.github.specdrivendesign.lql.pkg.ast.Expression;
import com.github.specdrivendesign.lql.pkg.env.Env;
import com.github.specdrivendesign.lql.pkg.errors.Errors;
import com.github.specdrivendesign.lql.pkg.tokens.Tokens;
import com.github.specdrivendesign.lql.pkg.types.Types;

import java.util.Map;

public class Binary implements Expression {
    private Expression left;
    private int operator; // token type as defined in Tokens
    private Expression right;
    private int line;
    private int column;

    public Binary(Expression left, int operator, Expression right, int line, int column) {
        this.left = left;
        this.operator = operator;
        this.right = right;
        this.line = line;
        this.column = column;
    }

    @Override
    public Object eval(Map<String, Object> ctx, Env env) throws Exception {
        // Handle logical AND (short-circuit)
        if (operator == Tokens.TokenAnd) {
            Object leftVal = left.eval(ctx, env);
            if (!(leftVal instanceof Boolean)) {
                throw Errors.newSemanticError("AND operator requires boolean operand", line, column);
            }
            if (!((Boolean) leftVal)) {
                return false;
            }
            Object rightVal = right.eval(ctx, env);
            if (!(rightVal instanceof Boolean)) {
                throw Errors.newSemanticError("AND operator requires boolean operand", line, column);
            }
            return rightVal;
        }
        // Handle logical OR (short-circuit)
        else if (operator == Tokens.TokenOr) {
            Object leftVal = left.eval(ctx, env);
            if (!(leftVal instanceof Boolean)) {
                throw Errors.newSemanticError("OR operator requires boolean operand", line, column);
            }
            if (((Boolean) leftVal)) {
                return true;
            }
            Object rightVal = right.eval(ctx, env);
            if (!(rightVal instanceof Boolean)) {
                throw Errors.newSemanticError("OR operator requires boolean operand", line, column);
            }
            return rightVal;
        }
        // For other operators, evaluate both operands.
        else {
            Object leftVal = left.eval(ctx, env);
            Object rightVal = right.eval(ctx, env);
            switch (operator) {
                case Tokens.TokenPlus: {
                    Double ln = Types.toFloat(leftVal);
                    Double rn = Types.toFloat(rightVal);
                    if (ln == null || rn == null) {
                        throw Errors.newSemanticError("'+' operator used on non‑numeric type", line, column);
                    }
                    if (Types.isInt(leftVal) != Types.isInt(rightVal)) {
                        throw Errors.newSemanticError("Mixed numeric types require explicit conversion", line, column);
                    }
                    if (Types.isInt(leftVal)) {
                        return (long) (ln + rn);
                    }
                    return ln + rn;
                }
                case Tokens.TokenMinus: {
                    Double ln = Types.toFloat(leftVal);
                    Double rn = Types.toFloat(rightVal);
                    if (ln == null || rn == null) {
                        throw Errors.newSemanticError("'-' operator used on non‑numeric type", line, column);
                    }
                    if (Types.isInt(leftVal) != Types.isInt(rightVal)) {
                        throw Errors.newSemanticError("Mixed numeric types require explicit conversion", line, column);
                    }
                    if (Types.isInt(leftVal)) {
                        return (long) (ln - rn);
                    }
                    return ln - rn;
                }
                case Tokens.TokenMultiply: {
                    Double ln = Types.toFloat(leftVal);
                    Double rn = Types.toFloat(rightVal);
                    if (ln == null || rn == null) {
                        throw Errors.newSemanticError("'*' operator used on non‑numeric type", line, column);
                    }
                    if (Types.isInt(leftVal) != Types.isInt(rightVal)) {
                        throw Errors.newSemanticError("Mixed numeric types require explicit conversion", line, column);
                    }
                    if (Types.isInt(leftVal)) {
                        return (long) (ln * rn);
                    }
                    return ln * rn;
                }
                case Tokens.TokenDivide: {
                    Double ln = Types.toFloat(leftVal);
                    Double rn = Types.toFloat(rightVal);
                    if (ln == null || rn == null) {
                        throw Errors.newSemanticError("'/' operator used on non‑numeric type", line, column);
                    }
                    if (rn == 0) {
                        throw Errors.newDivideByZeroError("division by zero", line, column);
                    }
                    if (Types.isInt(leftVal) != Types.isInt(rightVal)) {
                        throw Errors.newSemanticError("Mixed numeric types require explicit conversion", line, column);
                    }
                    if (Types.isInt(leftVal)) {
                        return (long) (ln / rn);
                    }
                    return ln / rn;
                }
                case Tokens.TokenModulo: {
                    Long ln = Types.toInt(leftVal);
                    Long rn = Types.toInt(rightVal);
                    if (ln == null || rn == null) {
                        throw Errors.newSemanticError("'%' operator used on non‑numeric type", line, column);
                    }
                    return ln % rn;
                }
                case Tokens.TokenLt:
                    return Types.compare(leftVal, rightVal, "<", line, column);
                case Tokens.TokenGt:
                    return Types.compare(leftVal, rightVal, ">", line, column);
                case Tokens.TokenLte:
                    return Types.compare(leftVal, rightVal, "<=", line, column);
                case Tokens.TokenGte:
                    return Types.compare(leftVal, rightVal, ">=", line, column);
                case Tokens.TokenEq:
                    return Types.equals(leftVal, rightVal);
                case Tokens.TokenNeq:
                    return !Types.equals(leftVal, rightVal);
                default:
                    throw Errors.newUnknownOperatorError("unknown binary operator", line, column);
            }
        }
    }

    @Override
    public int[] pos() {
        return new int[]{line, column};
    }

    @Override
    public String toString() {
        String leftStr = left.toString();
        String rightStr = right.toString();
        String opStr = Tokens.getFixedTokenLiteral(operator);
        if (Color.isEnabled()) {
            opStr = Color.getOperatorColor() + opStr + Color.getReset();
        }
        return String.format("%s %s %s", leftStr, opStr, rightStr);
    }
}
