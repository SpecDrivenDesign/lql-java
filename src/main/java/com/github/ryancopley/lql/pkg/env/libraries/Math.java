// --------- FILE START: "Math.java" (converted from pkg/env/libraries/math.go) ----------
package com.github.ryancopley.lql.pkg.env.libraries;

import com.github.ryancopley.lql.pkg.env.ILibrary;
import com.github.ryancopley.lql.pkg.errors.Errors;
import com.github.ryancopley.lql.pkg.param.Param;
import com.github.ryancopley.lql.pkg.types.Types;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Math implements ILibrary {

    public Math() {
    }

    @Override
    public Object call(String functionName, List<Param> args, int line, int col, int parenLine, int parenCol) throws Exception {
        switch (functionName) {
            case "abs": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("math.abs requires 1 argument", line, col);
                }
                Param arg0 = args.get(0);
                Double num = Types.toFloat(arg0.getValue());
                if (num == null) {
                    throw Errors.newTypeError("math.abs: argument must be numeric", arg0.getLine(), arg0.getColumn());
                }
                double absVal = num < 0 ? -num : num;
                if (Types.isInt(arg0.getValue())) {
                    return (long) absVal;
                }
                return absVal;
            }
            case "sqrt": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("math.sqrt requires 1 argument", line, col);
                }
                Param arg0 = args.get(0);
                Double num = Types.toFloat(arg0.getValue());
                if (num == null) {
                    throw Errors.newTypeError("math.sqrt: argument must be numeric", arg0.getLine(), arg0.getColumn());
                }
                if (num < 0) {
                    throw Errors.newFunctionCallError("math.sqrt: argument must be non‑negative", arg0.getLine(), arg0.getColumn());
                }
                return java.lang.Math.sqrt(num);
            }
            case "floor": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("math.floor requires 1 argument", line, col);
                }
                Param arg0 = args.get(0);
                Double num = Types.toFloat(arg0.getValue());
                if (num == null) {
                    throw Errors.newTypeError("math.floor: argument must be numeric", arg0.getLine(), arg0.getColumn());
                }
                return java.lang.Math.floor(num);
            }
            case "round": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("math.round requires 1 argument", line, col);
                }
                Param arg0 = args.get(0);
                Double num = Types.toFloat(arg0.getValue());
                if (num == null) {
                    throw Errors.newTypeError("math.round: argument must be numeric", arg0.getLine(), arg0.getColumn());
                }
                if (num < 0) {
                    return -java.lang.Math.round(-num);
                }
                return java.lang.Math.round(num);
            }

            case "ceil": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("math.ceil requires 1 argument", line, col);
                }
                Param arg0 = args.get(0);
                Double num = Types.toFloat(arg0.getValue());
                if (num == null) {
                    throw Errors.newTypeError("math.ceil: argument must be numeric", arg0.getLine(), arg0.getColumn());
                }
                return java.lang.Math.ceil(num);
            }
            case "pow": {
                if (args.size() != 2) {
                    throw Errors.newParameterError("math.pow requires 2 arguments", line, col);
                }
                Param arg0 = args.get(0);
                Double base = Types.toFloat(arg0.getValue());
                if (base == null) {
                    throw Errors.newTypeError("math.pow: first argument must be numeric", arg0.getLine(), arg0.getColumn());
                }
                Param arg1 = args.get(1);
                Double exp = Types.toFloat(arg1.getValue());
                if (exp == null) {
                    throw Errors.newTypeError("math.pow: second argument must be numeric", arg1.getLine(), arg1.getColumn());
                }
                return java.lang.Math.pow(base, exp);
            }
            case "sum": {
                if (args.size() < 1 || args.size() > 3) {
                    throw Errors.newParameterError("math.sum requires 1 to 3 arguments", parenLine, parenCol);
                }
                Param arg0 = args.get(0);
                List<Object> arr = Types.convertToInterfaceList(arg0.getValue());
                if (arr == null) {
                    throw Errors.newTypeError("math.sum: first argument must be an array", arg0.getLine(), arg0.getColumn());
                }
                String subfield = "";
                Object defaultVal = null;
                if (args.size() >= 2) {
                    Param arg1 = args.get(1);
                    Object sf = arg1.getValue();
                    if (!(sf instanceof String)) {
                        throw Errors.newTypeError("math.sum: second argument must be string", arg1.getLine(), arg1.getColumn());
                    }
                    subfield = (String) sf;
                }
                if (args.size() == 3) {
                    defaultVal = args.get(2).getValue();
                }
                double sum = 0.0;
                Boolean firstIsInt = null;
                for (Object elem : arr) {
                    Object numObj;
                    if (!subfield.isEmpty()) {
                        var obj = Types.convertToStringMap(elem);
                        if (obj == null) {
                            if (defaultVal != null) {
                                numObj = defaultVal;
                            } else {
                                throw Errors.newFunctionCallError("math.sum: element is not an object and subfield specified", arg0.getLine(), arg0.getColumn());
                            }
                        } else {
                            if (obj.containsKey(subfield)) {
                                numObj = obj.get(subfield);
                            } else {
                                if (defaultVal != null) {
                                    numObj = defaultVal;
                                } else {
                                    throw Errors.newFunctionCallError("math.sum: field '" + subfield + "' missing in element", arg0.getLine(), arg0.getColumn());
                                }
                            }
                        }
                    } else {
                        numObj = elem;
                    }
                    Double nf = Types.toFloat(numObj);
                    if (nf == null) {
                        throw Errors.newTypeError("math.sum: element is not numeric", arg0.getLine(), arg0.getColumn());
                    }
                    if (firstIsInt == null) {
                        firstIsInt = Types.isInt(numObj);
                    } else {
                        if (Types.isInt(numObj) != firstIsInt) {
                            throw Errors.newSemanticError("Mixed numeric types require explicit conversion", arg0.getLine(), arg0.getColumn());
                        }
                    }
                    sum += nf;
                }
                if (firstIsInt != null && firstIsInt) {
                    return (long) sum;
                }
                return sum;
            }
            case "min": {
                if (args.size() < 1 || args.size() > 3) {
                    throw Errors.newParameterError("math.min requires 1 to 3 arguments", parenLine, parenCol);
                }
                Param arg0 = args.get(0);
                List<Object> arr = Types.convertToInterfaceList(arg0.getValue());
                if (arr == null) {
                    throw Errors.newTypeError("math.min: first argument must be an array", arg0.getLine(), arg0.getColumn());
                }
                String subfield = "";
                Object defaultVal = null;
                if (args.size() >= 2) {
                    Param arg1 = args.get(1);
                    Object sf = arg1.getValue();
                    if (!(sf instanceof String)) {
                        throw Errors.newTypeError("math.min: second argument must be string", arg1.getLine(), arg1.getColumn());
                    }
                    subfield = (String) sf;
                }
                if (args.size() == 3) {
                    defaultVal = args.get(2).getValue();
                }
                if (arr.isEmpty()) {
                    if (defaultVal != null) {
                        return defaultVal;
                    }
                    throw Errors.newFunctionCallError("math.min: array is empty", arg0.getLine(), arg0.getColumn());
                }
                Double m = null;
                Boolean firstIsInt = null;
                boolean first = true;
                for (Object elem : arr) {
                    Object numObj;
                    if (!subfield.isEmpty()) {
                        var obj = Types.convertToStringMap(elem);
                        if (obj == null) {
                            if (defaultVal != null) {
                                numObj = defaultVal;
                            } else {
                                throw Errors.newFunctionCallError("math.min: element is not an object and subfield specified", arg0.getLine(), arg0.getColumn());
                            }
                        } else {
                            if (obj.containsKey(subfield)) {
                                numObj = obj.get(subfield);
                            } else {
                                if (defaultVal != null) {
                                    numObj = defaultVal;
                                } else {
                                    throw Errors.newFunctionCallError("math.min: field '" + subfield + "' missing in element", arg0.getLine(), arg0.getColumn());
                                }
                            }
                        }
                    } else {
                        numObj = elem;
                    }
                    Double nf = Types.toFloat(numObj);
                    if (nf == null) {
                        throw Errors.newTypeError("math.min: element is not numeric", arg0.getLine(), arg0.getColumn());
                    }
                    if (firstIsInt == null) {
                        firstIsInt = Types.isInt(numObj);
                    } else {
                        if (Types.isInt(numObj) != firstIsInt) {
                            throw Errors.newSemanticError("Mixed numeric types require explicit conversion", arg0.getLine(), arg0.getColumn());
                        }
                    }
                    if (first) {
                        m = nf;
                        first = false;
                    } else {
                        if (nf < m) {
                            m = nf;
                        }
                    }
                }
                if (firstIsInt != null && firstIsInt) {
                    return (long) m.doubleValue();
                }
                return m;
            }
            case "max": {
                if (args.size() < 1 || args.size() > 3) {
                    throw Errors.newParameterError("math.max requires 1 to 3 arguments", parenLine, parenCol);
                }
                Param arg0 = args.get(0);
                List<Object> arr = Types.convertToInterfaceList(arg0.getValue());
                if (arr == null) {
                    throw Errors.newTypeError("math.max: first argument must be an array", arg0.getLine(), arg0.getColumn());
                }
                String subfield = "";
                Object defaultVal = null;
                if (args.size() >= 2) {
                    Param arg1 = args.get(1);
                    Object sf = arg1.getValue();
                    if (!(sf instanceof String)) {
                        throw Errors.newTypeError("math.max: second argument must be string", arg1.getLine(), arg1.getColumn());
                    }
                    subfield = (String) sf;
                }
                if (args.size() == 3) {
                    defaultVal = args.get(2).getValue();
                }
                if (arr.isEmpty()) {
                    if (defaultVal != null) {
                        return defaultVal;
                    }
                    throw Errors.newFunctionCallError("math.max: array is empty", arg0.getLine(), arg0.getColumn());
                }
                Double m = null;
                Boolean firstIsInt = null;
                boolean first = true;
                for (Object elem : arr) {
                    Object numObj;
                    if (!subfield.isEmpty()) {
                        var obj = Types.convertToStringMap(elem);
                        if (obj == null) {
                            if (defaultVal != null) {
                                numObj = defaultVal;
                            } else {
                                throw Errors.newFunctionCallError("math.max: element is not an object and subfield specified", arg0.getLine(), arg0.getColumn());
                            }
                        } else {
                            if (obj.containsKey(subfield)) {
                                numObj = obj.get(subfield);
                            } else {
                                if (defaultVal != null) {
                                    numObj = defaultVal;
                                } else {
                                    throw Errors.newFunctionCallError("math.max: field '" + subfield + "' missing in element", arg0.getLine(), arg0.getColumn());
                                }
                            }
                        }
                    } else {
                        numObj = elem;
                    }
                    Double nf = Types.toFloat(numObj);
                    if (nf == null) {
                        throw Errors.newTypeError("math.max: element is not numeric", arg0.getLine(), arg0.getColumn());
                    }
                    if (firstIsInt == null) {
                        firstIsInt = Types.isInt(numObj);
                    } else {
                        if (Types.isInt(numObj) != firstIsInt) {
                            throw Errors.newSemanticError("Mixed numeric types require explicit conversion", arg0.getLine(), arg0.getColumn());
                        }
                    }
                    if (first) {
                        m = nf;
                        first = false;
                    } else {
                        if (nf > m) {
                            m = nf;
                        }
                    }
                }
                if (firstIsInt != null && firstIsInt) {
                    return (long) m.doubleValue();
                }
                return m;
            }
            case "avg": {
                if (args.size() < 1 || args.size() > 3) {
                    throw Errors.newParameterError("math.avg requires 1 to 3 arguments", parenLine, parenCol);
                }
                Param arg0 = args.get(0);
                List<Object> arr = Types.convertToInterfaceList(arg0.getValue());
                if (arr == null) {
                    throw Errors.newTypeError("math.avg: first argument must be an array", arg0.getLine(), arg0.getColumn());
                }
                String subfield = "";
                Object defaultVal = null;
                if (args.size() >= 2) {
                    Param arg1 = args.get(1);
                    Object sf = arg1.getValue();
                    if (!(sf instanceof String)) {
                        throw Errors.newTypeError("math.avg: second argument must be string", arg1.getLine(), arg1.getColumn());
                    }
                    subfield = (String) sf;
                }
                if (args.size() == 3) {
                    defaultVal = args.get(2).getValue();
                }
                if (arr.isEmpty()) {
                    if (defaultVal != null) {
                        return defaultVal;
                    }
                    throw Errors.newFunctionCallError("math.avg: array is empty", arg0.getLine(), arg0.getColumn());
                }
                double sum = 0.0;
                int count = 0;
                Boolean firstIsInt = null;
                for (Object elem : arr) {
                    Object numObj;
                    if (!subfield.isEmpty()) {
                        var obj = Types.convertToStringMap(elem);
                        if (obj == null) {
                            throw Errors.newFunctionCallError("math.avg: element is not an object and subfield specified", arg0.getLine(), arg0.getColumn());
                        }
                        if (obj.containsKey(subfield)) {
                            numObj = obj.get(subfield);
                        } else {
                            throw Errors.newFunctionCallError("math.avg: field '" + subfield + "' missing in element", arg0.getLine(), arg0.getColumn());
                        }
                    } else {
                        numObj = elem;
                    }
                    Double nf = Types.toFloat(numObj);
                    if (nf == null) {
                        throw Errors.newTypeError("math.avg: element is not numeric", arg0.getLine(), arg0.getColumn());
                    }
                    if (firstIsInt == null) {
                        firstIsInt = Types.isInt(numObj);
                    } else {
                        if (Types.isInt(numObj) != firstIsInt) {
                            throw Errors.newSemanticError("Mixed numeric types require explicit conversion", arg0.getLine(), arg0.getColumn());
                        }
                    }
                    sum += nf;
                    count++;
                }
                return sum / count;
            }
            default:
                throw Errors.newFunctionCallError("unknown math function '" + functionName + "'", 0, 0);
        }
    }
}
// --------- FILE END: "Math.java" ----------
