// --------- FILE START: "StringLib.java" (converted from pkg/env/libraries/string.go) ----------
package com.github.specdrivendesign.lql.pkg.env.libraries;

import com.github.specdrivendesign.lql.pkg.env.ILibrary;
import com.github.specdrivendesign.lql.pkg.errors.Errors;
import com.github.specdrivendesign.lql.pkg.param.Param;
import com.github.specdrivendesign.lql.pkg.types.Types;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.ArrayList;
import java.util.List;

public class StringLib implements ILibrary {

    public StringLib() {
    }

    @Override
    public Object call(String functionName, List<Param> args, int line, int col, int parenLine, int parenCol) throws Exception {
        switch (functionName) {
            case "concat": {
                if (args.size() < 1) {
                    throw Errors.newParameterError("string.concat requires at least 1 argument", parenLine, parenCol);
                }
                StringBuilder sb = new StringBuilder();
                for (Param arg : args) {
                    Object s = arg.getValue();
                    if (!(s instanceof String)) {
                        throw Errors.newTypeError("string.concat: all arguments must be strings", arg.getLine(), arg.getColumn());
                    }
                    sb.append((String) s);
                }
                return sb.toString();
            }
            case "toLower": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("string.toLower requires 1 argument", line, col);
                }
                Param arg0 = args.get(0);
                Object s = arg0.getValue();
                if (!(s instanceof String)) {
                    throw Errors.newTypeError("string.toLower: argument must be string", arg0.getLine(), arg0.getColumn());
                }
                return ((String) s).toLowerCase();
            }
            case "toUpper": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("string.toUpper requires 1 argument", line, col);
                }
                Param arg0 = args.get(0);
                Object s = arg0.getValue();
                if (!(s instanceof String)) {
                    throw Errors.newTypeError("string.toUpper: argument must be string", arg0.getLine(), arg0.getColumn());
                }
                return ((String) s).toUpperCase();
            }
            case "trim": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("string.trim requires 1 argument", line, col);
                }
                Param arg0 = args.get(0);
                Object s = arg0.getValue();
                if (!(s instanceof String)) {
                    throw Errors.newTypeError("string.trim: argument must be string", arg0.getLine(), arg0.getColumn());
                }
                return ((String) s).trim();
            }
            case "startsWith": {
                if (args.size() != 2) {
                    throw Errors.newParameterError("string.startsWith requires 2 arguments", line, col);
                }
                Param arg0 = args.get(0);
                Param arg1 = args.get(1);
                Object s = arg0.getValue();
                Object prefix = arg1.getValue();
                if (!(s instanceof String)) {
                    throw Errors.newTypeError("string.startsWith: first argument must be string", arg0.getLine(), arg0.getColumn());
                }
                if (!(prefix instanceof String)) {
                    throw Errors.newTypeError("string.startsWith: second argument must be string", arg1.getLine(), arg1.getColumn());
                }
                return ((String) s).startsWith((String) prefix);
            }
            case "endsWith": {
                if (args.size() != 2) {
                    throw Errors.newParameterError("string.endsWith requires 2 arguments", line, col);
                }
                Param arg0 = args.get(0);
                Param arg1 = args.get(1);
                Object s = arg0.getValue();
                Object suffix = arg1.getValue();
                if (!(s instanceof String)) {
                    throw Errors.newTypeError("string.endsWith: first argument must be string", arg0.getLine(), arg0.getColumn());
                }
                if (!(suffix instanceof String)) {
                    throw Errors.newTypeError("string.endsWith: second argument must be string", arg1.getLine(), arg1.getColumn());
                }
                return ((String) s).endsWith((String) suffix);
            }
            case "contains": {
                if (args.size() != 2) {
                    throw Errors.newParameterError("string.contains requires 2 arguments", line, col);
                }
                Param arg0 = args.get(0);
                Param arg1 = args.get(1);
                Object s = arg0.getValue();
                Object substr = arg1.getValue();
                if (!(s instanceof String)) {
                    throw Errors.newTypeError("string.contains: first argument must be string", arg0.getLine(), arg0.getColumn());
                }
                if (!(substr instanceof String)) {
                    throw Errors.newTypeError("string.contains: second argument must be string", arg1.getLine(), arg1.getColumn());
                }
                return ((String) s).contains((String) substr);
            }
            case "split": {
                if (args.size() != 2) {
                    throw Errors.newParameterError("string.split requires 2 arguments", line, col);
                }
                Param arg0 = args.get(0);
                Param arg1 = args.get(1);
                Object s = arg0.getValue();
                if (!(s instanceof String)) {
                    throw Errors.newTypeError("string.split: first argument must be string", arg0.getLine(), arg0.getColumn());
                }
                Object delimObj = arg1.getValue();
                if (!(delimObj instanceof String)) {
                    throw Errors.newTypeError("string.split: second argument must be string", arg1.getLine(), arg1.getColumn());
                }
                String delimiter = (String) delimObj;
                // Use Pattern.quote with a negative limit to preserve trailing empty strings.
                String[] parts = ((String) s).split(java.util.regex.Pattern.quote(delimiter), -1);
                List<String> result = new ArrayList<>();
                for (String part : parts) {
                    result.add(part);
                }
                return result;
            }

            case "join": {
                if (args.size() != 2) {
                    throw Errors.newParameterError("string.join requires 2 arguments", line, col);
                }
                Param arg0 = args.get(0);
                Param arg1 = args.get(1);
                Object arrObj = arg0.getValue();
                Object sepObj = arg1.getValue();
                if (!(arrObj instanceof List<?>)) {
                    throw Errors.newTypeError("string.join: first argument must be an array", arg0.getLine(), arg0.getColumn());
                }
                if (!(sepObj instanceof String)) {
                    throw Errors.newTypeError("string.join: second argument must be string", arg1.getLine(), arg1.getColumn());
                }
                List<?> arr = (List<?>) arrObj;
                String sep = (String) sepObj;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < arr.size(); i++) {
                    Object item = arr.get(i);
                    if (!(item instanceof String)) {
                        throw Errors.newTypeError("string.join: all array elements must be strings", arg0.getLine(), arg0.getColumn());
                    }
                    if (i > 0) {
                        sb.append(sep);
                    }
                    sb.append((String) item);
                }
                return sb.toString();
            }
            case "substring": {
                if (args.size() != 3) {
                    throw Errors.newParameterError("string.substring requires 3 arguments", line, col);
                }
                Param arg0 = args.get(0);
                Object sObj = arg0.getValue();
                if (!(sObj instanceof String)) {
                    throw Errors.newTypeError("string.substring: first argument must be a string", arg0.getLine(), arg0.getColumn());
                }
                String s = (String) sObj;
                Param arg1 = args.get(1);
                Param arg2 = args.get(2);
                Long start = Types.toInt(arg1.getValue());
                Long length = Types.toInt(arg2.getValue());
                if (start == null) {
                    throw Errors.newTypeError("string.substring: second argument must be an integer", arg1.getLine(), arg1.getColumn());
                }
                if (length == null) {
                    throw Errors.newTypeError("string.substring: third argument must be an integer", arg2.getLine(), arg2.getColumn());
                }
                int sLength = s.length();
                int startIdx = start.intValue();
                if (startIdx < 0 || startIdx >= sLength) {
                    throw Errors.newFunctionCallError("string.substring: start index out of bounds", arg1.getLine(), arg1.getColumn());
                }
                int endIdx = startIdx + length.intValue();
                if (endIdx > sLength) {
                    endIdx = sLength;
                }
                return s.substring(startIdx, endIdx);
            }
            case "replace": {
                if (args.size() < 3 || args.size() > 4) {
                    Param lastArg = args.get(args.size()-1);
                    throw Errors.newParameterError("string.replace requires 3 or 4 arguments", lastArg.getLine(), lastArg.getColumn());
                }
                Param arg0 = args.get(0);
                Param arg1 = args.get(1);
                Param arg2 = args.get(2);
                Object sObj = arg0.getValue();
                Object oldObj = arg1.getValue();
                Object newObj = arg2.getValue();
                if (!(sObj instanceof String)) {
                    throw Errors.newTypeError("string.replace: first argument must be a string", arg0.getLine(), arg0.getColumn());
                }
                if (!(oldObj instanceof String)) {
                    throw Errors.newTypeError("string.replace: second argument must be a string", arg1.getLine(), arg1.getColumn());
                }
                if (!(newObj instanceof String)) {
                    throw Errors.newTypeError("string.replace: third argument must be a string", arg2.getLine(), arg2.getColumn());
                }
                String s = (String) sObj;
                String oldStr = (String) oldObj;
                String newStr = (String) newObj;
                int limit = -1;
                if (args.size() == 4) {
                    Param arg3 = args.get(3);
                    Long lArg = Types.toInt(arg3.getValue());
                    if (lArg == null) {
                        throw Errors.newTypeError("string.replace: fourth argument must be numeric", arg3.getLine(), arg3.getColumn());
                    }
                    limit = lArg.intValue();
                }
                if (limit < 0) {
                    return s.replace(oldStr, newStr);
                }
                String result = s;
                for (int i = 0; i < limit; i++) {
                    if (!result.contains(oldStr)) {
                        break;
                    }
                    result = result.replaceFirst(Pattern.quote(oldStr), Matcher.quoteReplacement(newStr));
                }
                return result;
            }
            case "indexOf": {
                if (args.size() < 2 || args.size() > 3) {
                    Param lastArg = args.get(args.size()-1);
                    throw Errors.newParameterError("string.indexOf requires 2 or 3 arguments", lastArg.getLine(), lastArg.getColumn());
                }
                Param arg0 = args.get(0);
                Param arg1 = args.get(1);
                Object sObj = arg0.getValue();
                Object substrObj = arg1.getValue();
                if (!(sObj instanceof String)) {
                    throw Errors.newTypeError("string.indexOf: first argument must be a string", arg0.getLine(), arg0.getColumn());
                }
                if (!(substrObj instanceof String)) {
                    throw Errors.newTypeError("string.indexOf: second argument must be a string", arg1.getLine(), arg1.getColumn());
                }
                String s = (String) sObj;
                String substr = (String) substrObj;
                int fromIndex = 0;
                if (args.size() == 3) {
                    Param arg2 = args.get(2);
                    Long idx = Types.toInt(arg2.getValue());
                    if (idx == null) {
                        throw Errors.newTypeError("string.indexOf: third argument must be numeric", arg2.getLine(), arg2.getColumn());
                    }
                    fromIndex = idx.intValue();
                }
                if (fromIndex < 0 || fromIndex >= s.length()) {
                    return -1;
                }
                int index = s.indexOf(substr, fromIndex);
                return index >= 0 ? index : -1;
            }
            default:
                throw Errors.newFunctionCallError("unknown string function '" + functionName + "'", 0, 0);
        }
    }
}
// --------- FILE END: "StringLib.java" ----------
