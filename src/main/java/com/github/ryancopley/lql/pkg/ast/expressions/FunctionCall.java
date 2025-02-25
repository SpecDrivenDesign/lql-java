package com.github.ryancopley.lql.pkg.ast.expressions;

import com.github.ryancopley.lql.pkg.ast.Expression;
import com.github.ryancopley.lql.pkg.env.Env;
import com.github.ryancopley.lql.pkg.errors.Errors;
import com.github.ryancopley.lql.pkg.param.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FunctionCall implements Expression {
    private List<String> namespace;
    private List<Expression> args;
    private int line;
    private int column;
    private int parenLine;
    private int parenColumn;

    public FunctionCall(List<String> namespace, List<Expression> args, int line, int column, int parenLine, int parenColumn) {
        this.namespace = namespace;
        this.args = args;
        this.line = line;
        this.column = column;
        this.parenLine = parenLine;
        this.parenColumn = parenColumn;
    }

    @Override
    public Object eval(Map<String, Object> ctx, Env env) throws Exception {
        if (namespace.size() < 2) {
            throw Errors.newParameterError("function call missing namespace", line, column);
        }
        String libName = namespace.get(0);
        String funcName = namespace.get(1);
        var lib = env.getLibrary(libName);
        if (lib == null) {
            throw Errors.newReferenceError(String.format("library '%s' not found", libName), line, column);
        }
        List<Param> evaluatedArgs = new ArrayList<>();
        for (Expression argExpr : args) {
            Object val = argExpr.eval(ctx, env);
            int[] pos = argExpr.pos();
            evaluatedArgs.add(new Param(val, pos[0], pos[1]));
        }
        return lib.call(funcName, evaluatedArgs, line, column, parenLine, parenColumn);
    }

    @Override
    public int[] pos() {
        return new int[]{line, column};
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (namespace.isEmpty()) {
            return "(missing function call)";
        }
        String libraryName = namespace.get(0);
        if (Color.isEnabled()) {
            libraryName = Color.getLibraryColor() + libraryName + Color.getReset();
        }
        String functionName = "";
        if (namespace.size() > 1) {
            List<String> rest = namespace.subList(1, namespace.size());
            String fnStr = String.join(".", rest);
            if (Color.isEnabled()) {
                fnStr = Color.getFunctionColor() + fnStr + Color.getReset();
            }
            String dot = ".";
            if (Color.isEnabled()) {
                dot = Color.getPunctuationColor() + "." + Color.getReset();
            }
            functionName = dot + fnStr;
        }
        String openParen = "(";
        String closeParen = ")";
        String comma = ", ";
        if (Color.isEnabled()) {
            openParen = Color.getPunctuationColor() + "(" + Color.getReset();
            closeParen = Color.getPunctuationColor() + ")" + Color.getReset();
            comma = Color.getPunctuationColor() + "," + Color.getReset() + " ";
        }
        sb.append(libraryName);
        sb.append(functionName);
        sb.append(openParen);
        for (int i = 0; i < args.size(); i++) {
            if (i > 0) {
                sb.append(comma);
            }
            sb.append(args.get(i).toString());
        }
        sb.append(closeParen);
        return sb.toString();
    }
}
