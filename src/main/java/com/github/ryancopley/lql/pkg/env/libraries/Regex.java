// --------- FILE START: "Regex.java" (converted from pkg/env/libraries/regex.go) ----------
package com.github.ryancopley.lql.pkg.env.libraries;

import com.github.ryancopley.lql.pkg.env.ILibrary;
import com.github.ryancopley.lql.pkg.errors.Errors;
import com.github.ryancopley.lql.pkg.param.Param;
import com.github.ryancopley.lql.pkg.types.Types;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex implements ILibrary {

    public Regex() {
    }

    @Override
    public Object call(String functionName, List<Param> args, int line, int col, int parenLine, int parenCol) throws Exception {
        switch (functionName) {
            case "match": {
                if (args.size() != 2) {
                    throw Errors.newParameterError("regex.match requires 2 arguments", line, col);
                }
                Param arg0 = args.get(0);
                Param arg1 = args.get(1);
                Object patternObj = arg0.getValue();
                if (!(patternObj instanceof String)) {
                    throw Errors.newTypeError("regex.match: first argument must be a string", arg0.getLine(), arg0.getColumn());
                }
                Object sObj = arg1.getValue();
                if (!(sObj instanceof String)) {
                    throw Errors.newTypeError("regex.match: second argument must be a string", arg1.getLine(), arg1.getColumn());
                }
                String pattern = (String) patternObj;
                String s = (String) sObj;
                Pattern re;
                try {
                    re = Pattern.compile(pattern);
                } catch (Exception e) {
                    throw Errors.newTypeError("regex.match: invalid pattern", arg0.getLine(), arg0.getColumn());
                }
                Matcher matcher = re.matcher(s);
                return matcher.find();
            }
            case "replace": {
                if (args.size() < 3 || args.size() > 4) {
                    Param lastArg = args.get(args.size()-1);
                    throw Errors.newParameterError("regex.replace requires 3 or 4 arguments", lastArg.getLine(), lastArg.getColumn());
                }
                Param arg0 = args.get(0);
                Param arg1 = args.get(1);
                Param arg2 = args.get(2);
                Object sObj = arg0.getValue();
                Object patternObj = arg1.getValue();
                Object replacementObj = arg2.getValue();
                if (!(sObj instanceof String)) {
                    throw Errors.newTypeError("regex.replace: first argument must be a string", arg0.getLine(), arg0.getColumn());
                }
                if (!(patternObj instanceof String)) {
                    throw Errors.newTypeError("regex.replace: second argument must be a string", arg1.getLine(), arg1.getColumn());
                }
                if (!(replacementObj instanceof String)) {
                    throw Errors.newTypeError("regex.replace: third argument must be a string", arg2.getLine(), arg2.getColumn());
                }
                String s = (String) sObj;
                String pattern = (String) patternObj;
                String replacement = (String) replacementObj;
                Pattern re;
                try {
                    re = Pattern.compile(pattern);
                } catch (Exception e) {
                    throw Errors.newTypeError("regex.replace: invalid pattern", arg1.getLine(), arg1.getColumn());
                }

                // Decide if we want capturing group substitution:
                String actualReplacement;
                if (!replacement.contains("$")) {
                    // Literal replacement: escape it.
                    actualReplacement = Matcher.quoteReplacement(replacement);
                } else {
                    // If replacement contains group references, use as is.
                    // However, if it ends with an odd number of backslashes, append one more to make it valid.
                    int backslashCount = 0;
                    for (int i = replacement.length() - 1; i >= 0; i--) {
                        if (replacement.charAt(i) == '\\') {
                            backslashCount++;
                        } else {
                            break;
                        }
                    }
                    if (backslashCount % 2 != 0) {
                        replacement = replacement + "\\";
                    }
                    actualReplacement = replacement;
                }

                if (args.size() == 3) {
                    return re.matcher(s).replaceAll(actualReplacement);
                }
                Param arg3 = args.get(3);
                Long lArg = Types.toInt(arg3.getValue());
                if (lArg == null) {
                    throw Errors.newTypeError("regex.replace: fourth argument must be numeric", arg3.getLine(), arg3.getColumn());
                }
                int limit = lArg.intValue();
                String result = s;
                for (int i = 0; i < limit; i++) {
                    Matcher matcher = re.matcher(result);
                    if (!matcher.find()) {
                        break;
                    }
                    result = matcher.replaceFirst(actualReplacement);
                }
                return result;
            }

            case "find": {
                if (args.size() != 2) {
                    throw Errors.newParameterError("regex.find requires 2 arguments", line, col);
                }
                Param arg0 = args.get(0);
                Param arg1 = args.get(1);
                Object patternObj = arg0.getValue();
                Object sObj = arg1.getValue();
                if (!(patternObj instanceof String)) {
                    throw Errors.newTypeError("regex.find: first argument must be a string", arg0.getLine(), arg0.getColumn());
                }
                if (!(sObj instanceof String)) {
                    throw Errors.newTypeError("regex.find: second argument must be a string", arg1.getLine(), arg1.getColumn());
                }
                String pattern = (String) patternObj;
                String s = (String) sObj;
                Pattern re;
                try {
                    re = Pattern.compile(pattern);
                } catch (Exception e) {
                    throw Errors.newTypeError("regex.find: invalid pattern", arg0.getLine(), arg0.getColumn());
                }
                Matcher matcher = re.matcher(s);
                if (matcher.find()) {
                    return matcher.group();
                }
                return "";
            }
            default:
                throw Errors.newFunctionCallError("unknown regex function '" + functionName + "'", 0, 0);
        }
    }
}
// --------- FILE END: "Regex.java" ----------
