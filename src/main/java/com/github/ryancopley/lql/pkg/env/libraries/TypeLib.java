// --------- FILE START: "TypeLib.java" (converted from pkg/env/libraries/type.go) ----------
package com.github.ryancopley.lql.pkg.env.libraries;

import com.github.ryancopley.lql.pkg.env.ILibrary;
import com.github.ryancopley.lql.pkg.errors.Errors;
import com.github.ryancopley.lql.pkg.param.Param;
import com.github.ryancopley.lql.pkg.types.Types;

import java.util.ArrayList;
import java.util.List;

public class TypeLib implements ILibrary {

    public TypeLib() {
    }

    @Override
    public Object call(String functionName, List<Param> args, int line, int col, int unused1, int unused2) throws Exception {
        switch (functionName) {
            case "string": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("type.string requires 1 argument", line, col);
                }
                Param arg0 = args.get(0);
                Object val = arg0.getValue();
                if (val == null) {
                    return "null";
                }
                if (val instanceof Number) {
                    double d = ((Number) val).doubleValue();
                    // If the number is whole, return it without a trailing .0
                    if (d == java.lang.Math.floor(d)) {
                        return String.valueOf((long) d);
                    }
                }
                return String.valueOf(val);
            }

            case "int": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("type.int requires 1 argument", line, col);
                }
                Param arg0 = args.get(0);
                Object val = arg0.getValue();
                if (val == null) {
                    return 0L;
                }
                if (val instanceof String) {
                    String s = ((String) val).trim();
                    if (s.length() >= 2 && ((s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') ||
                            (s.charAt(0) == '\'' && s.charAt(s.length() - 1) == '\''))) {
                        s = s.substring(1, s.length() - 1);
                    }
                    try {
                        return Long.parseLong(s);
                    } catch (NumberFormatException e) {
                        try {
                            Double f = Double.parseDouble(s);
                            return f.longValue();
                        } catch (NumberFormatException ex) {
                            throw Errors.newFunctionCallError("type.int: string '" + val + "' cannot be converted to int", arg0.getLine(), arg0.getColumn());
                        }
                    }
                } else {
                    Double num = Types.toFloat(val);
                    if (num == null) {
                        throw Errors.newTypeError("type.int: argument cannot be converted to int", arg0.getLine(), arg0.getColumn());
                    }
                    return num.longValue();
                }
            }
            case "float": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("type.float requires 1 argument", line, col);
                }
                Param arg0 = args.get(0);
                Object val = arg0.getValue();
                if (val == null) {
                    return 0.0;
                }
                if (val instanceof String) {
                    String s = ((String) val).trim();
                    try {
                        return Double.parseDouble(s);
                    } catch (NumberFormatException e) {
                        throw Errors.newFunctionCallError("type.float: string '" + val + "' cannot be converted to float", arg0.getLine(), arg0.getColumn());
                    }
                } else {
                    Double num = Types.toFloat(val);
                    if (num == null) {
                        throw Errors.newTypeError("type.float: argument cannot be converted to float", arg0.getLine(), arg0.getColumn());
                    }
                    return num;
                }
            }
            case "intArray": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("type.intArray requires 1 argument", line, col);
                }
                Object arrObj = args.get(0).getValue();
                List<Object> arr = Types.convertToInterfaceList(arrObj);
                if (arr == null) {
                    throw Errors.newFunctionCallError("intArray: value is not an array", args.get(0).getLine(), args.get(0).getColumn());
                }
                List<Object> result = new ArrayList<>();
                for (int i = 0; i < arr.size(); i++) {
                    Object elem = arr.get(i);
                    Long iVal = null;
                    if (elem instanceof String) {
                        try {
                            iVal = Long.parseLong(((String) elem).trim());
                        } catch (NumberFormatException e) {
                            throw Errors.newFunctionCallError("intArray: element at index " + i + " (" + elem + ") is not convertible to int", args.get(0).getLine(), args.get(0).getColumn());
                        }
                    } else {
                        iVal = Types.toInt(elem);
                    }
                    if (iVal == null) {
                        throw Errors.newFunctionCallError("intArray: element at index " + i + " (" + elem + ") is not convertible to int", args.get(0).getLine(), args.get(0).getColumn());
                    }
                    result.add(iVal);
                }
                return result;
            }
            case "floatArray": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("type.floatArray requires 1 argument", line, col);
                }
                Object arrObj = args.get(0).getValue();
                List<Object> arr = Types.convertToInterfaceList(arrObj);
                if (arr == null) {
                    throw Errors.newFunctionCallError("floatArray: value is not an array", args.get(0).getLine(), args.get(0).getColumn());
                }
                List<Object> result = new ArrayList<>();
                for (int i = 0; i < arr.size(); i++) {
                    Object elem = arr.get(i);
                    Double fVal = null;
                    if (elem instanceof String) {
                        try {
                            fVal = Double.parseDouble(((String) elem).trim());
                        } catch (NumberFormatException e) {
                            throw Errors.newFunctionCallError("floatArray: element at index " + i + " (" + elem + ") is not convertible to float", args.get(0).getLine(), args.get(0).getColumn());
                        }
                    } else {
                        fVal = Types.toFloat(elem);
                    }
                    if (fVal == null) {
                        throw Errors.newFunctionCallError("floatArray: element at index " + i + " (" + elem + ") is not convertible to float", args.get(0).getLine(), args.get(0).getColumn());
                    }
                    result.add(fVal);
                }
                return result;
            }case "stringArray": {
                 if (args.size() != 1) {
                     throw Errors.newParameterError("type.stringArray requires 1 argument", line, col);
                 }
                 Object arrObj = args.get(0).getValue();
                 List<Object> arr = Types.convertToInterfaceList(arrObj);
                 if (arr == null) {
                     throw Errors.newFunctionCallError("stringArray: value is not an array", args.get(0).getLine(), args.get(0).getColumn());
                 }
                 List<Object> result = new ArrayList<>();
                 for (Object elem : arr) {
                     String s;
                     if (elem instanceof Number) {
                         double d = ((Number) elem).doubleValue();
                         // If the number is whole, format without trailing ".0"
                         if (d == java.lang.Math.floor(d)) {
                             s = String.valueOf((long) d);
                         } else {
                             s = String.valueOf(d);
                         }
                     } else if (elem instanceof String) {
                         s = (String) elem;
                     } else if (elem == null) {
                         s = "null";
                     } else {
                         s = String.valueOf(elem);
                     }
                     result.add(s);
                 }
                 return result;
             }

            case "isNumber": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("type.isNumber requires 1 argument", line, col);
                }
                Param arg0 = args.get(0);
                Object val = arg0.getValue();
                if (val instanceof String) {
                    try {
                        Double.parseDouble(((String) val).trim());
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                } else {
                    return Types.toFloat(val) != null;
                }
            }
            case "isString": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("type.isString requires 1 argument", line, col);
                }
                return args.get(0).getValue() instanceof String;
            }
            case "isBoolean": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("type.isBoolean requires 1 argument", line, col);
                }
                return args.get(0).getValue() instanceof Boolean;
            }
            case "isArray": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("type.isArray requires 1 argument", line, col);
                }
                return Types.convertToInterfaceList(args.get(0).getValue()) != null;
            }
            case "isObject": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("type.isObject requires 1 argument", line, col);
                }
                return Types.convertToStringMap(args.get(0).getValue()) != null;
            }
            case "isNull": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("type.isNull requires 1 argument", line, col);
                }
                return args.get(0).getValue() == null;
            }
            default:
                throw Errors.newFunctionCallError("unknown type function '" + functionName + "'", 0, 0);
        }
    }
}
// --------- FILE END: "TypeLib.java" ----------
