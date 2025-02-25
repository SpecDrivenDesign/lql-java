// --------- FILE START: "Cond.java" (converted from pkg/env/libraries/cond.go) ----------
package com.github.ryancopley.lql.pkg.env.libraries;

import com.github.ryancopley.lql.pkg.env.ILibrary;
import com.github.ryancopley.lql.pkg.param.Param;
import com.github.ryancopley.lql.pkg.types.Types;
import com.github.ryancopley.lql.pkg.errors.Errors;

import java.util.List;
import java.util.Map;

public class Cond implements ILibrary {

    public Cond() {
    }

    @Override
    public Object call(String functionName, List<Param> args, int line, int col, int parenLine, int parenCol) throws Exception {
        switch (functionName) {
            case "ifExpr":
                if (args.size() != 3) {
                    throw Errors.newParameterError("cond.ifExpr requires 3 arguments", line, col);
                }
                Param arg0 = args.get(0);
                Object condValObj = arg0.getValue();
                boolean condVal;
                if (condValObj instanceof Boolean) {
                    condVal = (Boolean) condValObj;
                } else if (condValObj == null) {
                    condVal = false;
                } else {
                    throw Errors.newTypeError("cond.ifExpr: first argument must be boolean", arg0.getLine(), arg0.getColumn());
                }
                return condVal ? args.get(1).getValue() : args.get(2).getValue();

            case "coalesce":
                if (args.size() < 1) {
                    throw Errors.newParameterError("cond.coalesce requires at least 1 argument", parenLine, parenCol);
                }
                for (Param arg : args) {
                    if (arg.getValue() != null) {
                        return arg.getValue();
                    }
                }
                throw Errors.newFunctionCallError("cond.coalesce: all arguments are null", args.get(0).getLine(), args.get(0).getColumn());

            case "isFieldPresent":
                if (args.size() != 2) {
                    throw Errors.newParameterError("cond.isFieldPresent requires 2 arguments", line, col);
                }
                arg0 = args.get(0);
                Map<String, Object> obj = Types.convertToStringMap(arg0.getValue());
                if (obj == null) {
                    throw Errors.newTypeError("cond.isFieldPresent: first argument must be an object", arg0.getLine(), arg0.getColumn());
                }
                Param arg1 = args.get(1);
                Object fieldPathObj = arg1.getValue();
                if (!(fieldPathObj instanceof String)) {
                    throw Errors.newTypeError("cond.isFieldPresent: second argument must be a string", arg1.getLine(), arg1.getColumn());
                }
                String fieldPath = (String) fieldPathObj;
                return obj.containsKey(fieldPath);

            default:
                throw Errors.newFunctionCallError("unknown cond function '" + functionName + "'", 0, 0);
        }
    }
}
// --------- FILE END: "Cond.java" ----------
