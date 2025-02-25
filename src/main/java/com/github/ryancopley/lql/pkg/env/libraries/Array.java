// --------- FILE START: "Array.java" (converted from pkg/env/libraries/array.go) ----------
package com.github.ryancopley.lql.pkg.env.libraries;

import com.github.ryancopley.lql.pkg.env.ILibrary;
import com.github.ryancopley.lql.pkg.errors.Errors;
import com.github.ryancopley.lql.pkg.param.Param;
import com.github.ryancopley.lql.pkg.types.Types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Array implements ILibrary {

    public Array() {
    }

    @Override
    public Object call(String functionName, List<Param> args, int line, int col, int parenLine, int parenCol) throws Exception {
        switch (functionName) {
            case "contains":
                if (args.size() != 2) {
                    throw Errors.newParameterError("array.contains requires 2 arguments", line, col);
                }
                Param arg0 = args.get(0);
                List<Object> arr = Types.convertToInterfaceList(arg0.getValue());
                if (arr == null) {
                    throw Errors.newTypeError("array.contains: first argument must be an array", arg0.getLine(), arg0.getColumn());
                }
                Object target = args.get(1).getValue();
                for (Object item : arr) {
                    if (Types.equals(item, target)) {
                        return true;
                    }
                }
                return false;

                case "find":
                    if (args.size() < 3 || args.size() > 4) {
                        throw Errors.newParameterError("array.find requires 3 or 4 arguments", parenLine, parenCol);
                    }
                    arg0 = args.get(0);
                    arr = Types.convertToInterfaceList(arg0.getValue());
                    if (arr == null) {
                        throw Errors.newTypeError("array.find: first argument must be an array", arg0.getLine(), arg0.getColumn());
                    }
                    Param arg1 = args.get(1);
                    Object subfieldObj = arg1.getValue();
                    if (!(subfieldObj instanceof String)) {
                        throw Errors.newTypeError("array.find: second argument must be string", arg1.getLine(), arg1.getColumn());
                    }
                    String subfield = (String) subfieldObj;

                    Object matchVal = args.get(2).getValue();
                    Object defaultObj = null;
                    if (args.size() == 4) {
                        defaultObj = args.get(3).getValue();
                    }
                    for (Object elem : arr) {
                        Map<String, Object> obj = Types.convertToStringMap(elem);
                        if (obj == null) {
                            continue;
                        }
                        if (obj.containsKey(subfield)) {
                            Object v = obj.get(subfield);
                            if (Types.equals(v, matchVal)) {
                                return obj;
                            }
                        }
                    }
                    if (defaultObj != null) {
                        return defaultObj;
                    }
                    throw Errors.newFunctionCallError("array.find: no match found", arg0.getLine(), arg0.getColumn());

            case "first":
                if (args.size() < 1 || args.size() > 2) {
                    throw Errors.newParameterError("array.first requires 1 or 2 arguments", parenLine, parenCol);
                }
                arg0 = args.get(0);
                arr = Types.convertToInterfaceList(arg0.getValue());
                if (arr == null) {
                    throw Errors.newTypeError("array.first: argument must be an array", arg0.getLine(), arg0.getColumn());
                }
                if (arr.isEmpty()) {
                    if (args.size() == 2) {
                        return args.get(1).getValue();
                    }
                    throw Errors.newFunctionCallError("array.first: array is empty", arg0.getLine(), arg0.getColumn());
                }
                return arr.get(0);

            case "last":
                if (args.size() < 1 || args.size() > 2) {
                    throw Errors.newParameterError("array.last requires 1 or 2 arguments", parenLine, parenCol);
                }
                arg0 = args.get(0);
                arr = Types.convertToInterfaceList(arg0.getValue());
                if (arr == null) {
                    throw Errors.newTypeError("array.last: argument must be an array", arg0.getLine(), arg0.getColumn());
                }
                if (arr.isEmpty()) {
                    if (args.size() == 2) {
                        return args.get(1).getValue();
                    }
                    throw Errors.newFunctionCallError("array.last: array is empty", arg0.getLine(), arg0.getColumn());
                }
                return arr.get(arr.size() - 1);

            case "extract":
                if (args.size() < 2 || args.size() > 3) {
                    throw Errors.newParameterError("array.extract requires 2 or 3 arguments", parenLine, parenCol);
                }
                arg0 = args.get(0);
                arr = Types.convertToInterfaceList(arg0.getValue());
                if (arr == null) {
                    throw Errors.newTypeError("array.extract: argument must be an array", arg0.getLine(), arg0.getColumn());
                }
                arg1 = args.get(1);
                subfieldObj = arg1.getValue();
                if (!(subfieldObj instanceof String)) {
                    throw Errors.newTypeError("array.extract: second argument must be string", arg1.getLine(), arg1.getColumn());
                }
                subfield = (String) subfieldObj;
                Object defaultVal = null;
                if (args.size() == 3) {
                    defaultVal = args.get(2).getValue();
                }
                List<Object> extracted = new ArrayList<>();
                for (Object elem : arr) {
                    Map<String, Object> obj = Types.convertToStringMap(elem);
                    if (obj == null) {
                        extracted.add(defaultVal);
                    } else {
                        if (obj.containsKey(subfield)) {
                            extracted.add(obj.get(subfield));
                        } else {
                            extracted.add(defaultVal);
                        }
                    }
                }
                return extracted;

            case "sort":
                if (args.size() < 1 || args.size() > 2) {
                    throw Errors.newParameterError("array.sort requires 1 or 2 arguments", parenLine, parenCol);
                }
                arg0 = args.get(0);
                arr = Types.convertToInterfaceList(arg0.getValue());
                if (arr == null) {
                    throw Errors.newTypeError("array.sort: first argument must be an array", arg0.getLine(), arg0.getColumn());
                }
                boolean ascending = true;
                if (args.size() == 2) {
                    arg1 = args.get(1);
                    Object ascObj = arg1.getValue();
                    if (!(ascObj instanceof Boolean)) {
                        throw Errors.newTypeError("array.sort: second argument must be boolean", arg1.getLine(), arg1.getColumn());
                    }
                    ascending = (Boolean) ascObj;
                }
                if (arr.isEmpty()) {
                    return arr;
                }
                Object first = arr.get(0);
                boolean isNumeric = false;
                boolean isString = false;
                if (Types.toFloat(first) != null) {
                    isNumeric = true;
                } else if (first instanceof String) {
                    isString = true;
                } else {
                    throw Errors.newTypeError("array.sort: elements are not comparable", arg0.getLine(), arg0.getColumn());
                }

                final boolean finalAscending = ascending;
                final boolean finalIsNumeric = isNumeric;
                final boolean finalIsString = isString;

                List<Object> sorted = new ArrayList<>(arr);
                Collections.sort(sorted, new Comparator<Object>() {
                    @Override
                    public int compare(Object a, Object b) {
                        if (finalIsNumeric) {
                            Double af = Types.toFloat(a);
                            Double bf = Types.toFloat(b);
                            return finalAscending ? af.compareTo(bf) : bf.compareTo(af);
                        }
                        if (finalIsString) {
                            String as = (String) a;
                            String bs = (String) b;
                            return finalAscending ? as.compareTo(bs) : bs.compareTo(as);
                        }
                        return 0;
                    }
                });


                return sorted;

            case "flatten":
                if (args.size() != 1) {
                    throw Errors.newParameterError("array.flatten requires 1 argument", line, col);
                }
                arg0 = args.get(0);
                arr = Types.convertToInterfaceList(arg0.getValue());
                if (arr == null) {
                    throw Errors.newTypeError("array.flatten: argument must be an array", arg0.getLine(), arg0.getColumn());
                }
                List<Object> flat = new ArrayList<>();
                for (Object elem : arr) {
                    List<Object> subArr = Types.convertToInterfaceList(elem);
                    if (subArr != null) {
                        flat.addAll(subArr);
                    } else {
                        flat.add(elem);
                    }
                }
                return flat;

            case "filter":
                if (args.size() < 1 || args.size() > 3) {
                    throw Errors.newParameterError("array.filter requires between 1 and 3 arguments", line, col);
                }
                arg0 = args.get(0);
                arr = Types.convertToInterfaceList(arg0.getValue());
                if (arr == null) {
                    throw Errors.newTypeError("array.filter: first argument must be an array", arg0.getLine(), arg0.getColumn());
                }
                List<Object> filtered = new ArrayList<>();
                if (args.size() == 1) {
                    for (Object elem : arr) {
                        if (elem != null) {
                            filtered.add(elem);
                        }
                    }
                    return filtered;
                }
                arg1 = args.get(1);
                subfieldObj = arg1.getValue();
                if (!(subfieldObj instanceof String)) {
                    throw Errors.newTypeError("array.filter: subfield argument must be string", arg1.getLine(), arg1.getColumn());
                }
                subfield = (String) subfieldObj;
                if (args.size() == 2) {
                    for (Object elem : arr) {
                        Map<String, Object> obj = Types.convertToStringMap(elem);
                        if (obj != null && obj.containsKey(subfield) && obj.get(subfield) != null) {
                            filtered.add(elem);
                        }
                    }
                    return filtered;
                }
                Object matchValue = args.get(2).getValue();
                for (Object elem : arr) {
                    Map<String, Object> obj = Types.convertToStringMap(elem);
                    if (obj != null && obj.containsKey(subfield) && Types.equals(obj.get(subfield), matchValue)) {
                        filtered.add(elem);
                    }
                }
                return filtered;

            default:
                throw Errors.newFunctionCallError("unknown array function '" + functionName + "'", 0, 0);
        }
    }
}
// --------- FILE END: "Array.java" ----------
