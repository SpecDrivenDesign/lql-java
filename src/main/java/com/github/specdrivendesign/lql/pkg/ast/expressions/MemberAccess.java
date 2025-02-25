// --------- FILE START: "MemberAccess.java" (converted from pkg/ast/expressions/memberaccess.go) ----------
package com.github.specdrivendesign.lql.pkg.ast.expressions;

import com.github.specdrivendesign.lql.pkg.ast.Expression;
import com.github.specdrivendesign.lql.pkg.env.Env;
import com.github.specdrivendesign.lql.pkg.errors.Errors;
import com.github.specdrivendesign.lql.pkg.types.Types;

import java.util.List;
import java.util.Map;

public class MemberAccess implements Expression {
    private Expression target;
    private List<MemberPart> accessParts;

    public MemberAccess(Expression target, List<MemberPart> accessParts) {
        this.target = target;
        this.accessParts = accessParts;
    }

    @Override
    public Object eval(Map<String, Object> ctx, Env env) throws Exception {
        Object val = target.eval(ctx, env);
        for (MemberPart part : accessParts) {
            if (val == null && part.isOptional()) {
                return null;
            }
            if (part.isIndex()) {
                Object indexVal = part.getExpr().eval(ctx, env);
                // Try object access first.
                Map<String, Object> obj = Types.convertToStringMap(val);
                if (obj != null) {
                    String key = (indexVal instanceof String) ? (String) indexVal : String.valueOf(indexVal);
                    if (obj.containsKey(key)) {
                        val = obj.get(key);
                    } else {
                        if (part.isOptional()) {
                            return null;
                        }
                        throw Errors.newReferenceError("field '" + key + "' not found", part.getLine(), part.getColumn());
                    }
                } else {
                    // Try array access.
                    List<Object> arr = Types.convertToInterfaceList(val);
                    if (arr != null) {
                        Long idx = Types.toInt(indexVal);
                        if (idx == null) {
                            throw Errors.newTypeError("array index must be numeric", part.getLine(), part.getColumn());
                        }
                        int index = idx.intValue();
                        if (index < 0 || index >= arr.size()) {
                            if (part.isOptional()) {
                                return null;
                            }
                            throw Errors.newArrayOutOfBoundsError("array index out of bounds", part.getLine(), part.getColumn());
                        }
                        val = arr.get(index);
                    } else {
                        throw Errors.newTypeError("target is not an object or array", part.getLine(), part.getColumn());
                    }
                }
            } else {
                Map<String, Object> obj = Types.convertToStringMap(val);
                if (obj == null) {
                    throw Errors.newTypeError("dot access on non‑object", part.getLine(), part.getColumn());
                }
                if (obj.containsKey(part.getKey())) {
                    val = obj.get(part.getKey());
                } else {
                    if (part.isOptional()) {
                        return null;
                    }
                    throw Errors.newReferenceError("field '" + part.getKey() + "' not found", part.getLine(), part.getColumn());
                }
            }
        }
        return val;
    }

    @Override
    public int[] pos() {
        return target.pos();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(target.toString());
        for (MemberPart part : accessParts) {
            if (part.isOptional()) {
                if (Color.isEnabled()) {
                    sb.append(Color.getPunctuationColor()).append("?").append(Color.getReset());
                } else {
                    sb.append("?");
                }
            }
            if (part.isIndex()) {
                String openBracket = "[";
                String closeBracket = "]";
                if (Color.isEnabled()) {
                    openBracket = Color.getPunctuationColor() + "[" + Color.getReset();
                    closeBracket = Color.getPunctuationColor() + "]" + Color.getReset();
                }
                sb.append(openBracket);
                if (part.getExpr() != null) {
                    sb.append(part.getExpr().toString());
                }
                sb.append(closeBracket);
            } else {
                String dot = ".";
                if (Color.isEnabled()) {
                    dot = Color.getPunctuationColor() + "." + Color.getReset();
                }
                sb.append(dot);
                String keyStr = part.getKey();
                if (Color.isEnabled()) {
                    keyStr = Color.getContextColor() + keyStr + Color.getReset();
                }
                sb.append(keyStr);
            }
        }
        return sb.toString();
    }

    public static class MemberPart {
        private boolean optional;
        private boolean isIndex;
        private String key;
        private Expression expr;
        private int line;
        private int column;

        public MemberPart(boolean optional, boolean isIndex, String key, Expression expr, int line, int column) {
            this.optional = optional;
            this.isIndex = isIndex;
            this.key = key;
            this.expr = expr;
            this.line = line;
            this.column = column;
        }

        public boolean isOptional() {
            return optional;
        }

        public boolean isIndex() {
            return isIndex;
        }

        public String getKey() {
            return key;
        }

        public Expression getExpr() {
            return expr;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }
    }
}
// --------- FILE END: "MemberAccess.java" ----------
