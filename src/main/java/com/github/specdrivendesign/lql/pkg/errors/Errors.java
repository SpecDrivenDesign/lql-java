// --------- FILE START: "Errors.java" (converted from pkg/errors/errors.go) ----------
package com.github.specdrivendesign.lql.pkg.errors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Errors {

    public static class PositionalError extends Exception {
        private int line;
        private int column;
        private String kind;

        public PositionalError(String message, int line, int column, String kind) {
            super(message + " at line " + line + ", column " + column);
            this.line = line;
            this.column = column;
            this.kind = kind;
        }

        public int getLine() { return line; }
        public int getColumn() { return column; }
        public String getKind() { return kind; }
    }

    public static class TypeError extends PositionalError {
        public TypeError(String message, int line, int column) {
            super("TypeError: " + message, line, column, "TypeError");
        }
    }

    public static class DivideByZeroError extends PositionalError {
        public DivideByZeroError(String message, int line, int column) {
            super("DivideByZeroError: " + message, line, column, "DivideByZeroError");
        }
    }

    public static class ReferenceError extends PositionalError {
        public ReferenceError(String message, int line, int column) {
            super("ReferenceError: " + message, line, column, "ReferenceError");
        }
    }

    public static class UnknownIdentifierError extends PositionalError {
        public UnknownIdentifierError(String message, int line, int column) {
            super("UnknownIdentifierError: " + message, line, column, "UnknownIdentifierError");
        }
    }

    public static class UnknownOperatorError extends PositionalError {
        public UnknownOperatorError(String message, int line, int column) {
            super("UnknownOperatorError: " + message, line, column, "UnknownOperatorError");
        }
    }

    public static class FunctionCallError extends PositionalError {
        public FunctionCallError(String message, int line, int column) {
            super("FunctionCallError: " + message, line, column, "FunctionCallError");
        }
    }

    public static class ParameterError extends PositionalError {
        public ParameterError(String message, int line, int column) {
            super("ParameterError: " + message, line, column, "ParameterError");
        }
    }

    public static class LexicalError extends PositionalError {
        public LexicalError(String message, int line, int column) {
            super("LexicalError: " + message, line, column, "LexicalError");
        }
    }

    public static class SyntaxError extends PositionalError {
        public SyntaxError(String message, int line, int column) {
            super("SyntaxError: " + message, line, column, "SyntaxError");
        }
    }

    public static class SemanticError extends PositionalError {
        public SemanticError(String message, int line, int column) {
            super("SemanticError: " + message, line, column, "SemanticError");
        }
    }

    public static class ArrayOutOfBoundsError extends PositionalError {
        public ArrayOutOfBoundsError(String message, int line, int column) {
            super("ArrayOutOfBoundsError: " + message, line, column, "ArrayOutOfBoundsError");
        }
    }

    public static ParameterError newParameterError(String msg, int line, int column) {
        return new ParameterError(msg, line, column);
    }

    public static ReferenceError newReferenceError(String msg, int line, int column) {
        return new ReferenceError(msg, line, column);
    }

    public static TypeError newTypeError(String msg, int line, int column) {
        return new TypeError(msg, line, column);
    }

    public static FunctionCallError newFunctionCallError(String msg, int line, int column) {
        return new FunctionCallError(msg, line, column);
    }
    public static DivideByZeroError newDivideByZeroError(String msg, int line, int column) {
        return new DivideByZeroError(msg, line, column);
    }

    public static UnknownOperatorError newUnknownOperatorError(String msg, int line, int column) {
        return new UnknownOperatorError(msg, line, column);
    }
    public static UnknownIdentifierError newUnknownIdentifierError(String msg, int line, int column) {
        return new UnknownIdentifierError(msg, line, column);
    }

    public static LexicalError newLexicalError(String msg, int line, int column) {
        return new LexicalError(msg, line, column);
    }

    public static SyntaxError newSyntaxError(String msg, int line, int column) {
        return new SyntaxError(msg, line, column);
    }

    public static SemanticError newSemanticError(String msg, int line, int column) {
        return new SemanticError(msg, line, column);
    }

    public static ArrayOutOfBoundsError newArrayOutOfBoundsError(String msg, int line, int column) {
        return new ArrayOutOfBoundsError(msg, line, column);
    }

    public static String getErrorContext(String expr, int errLine, int errColumn, boolean colored) {
        String[] lines = expr.split("\n");
        if (errLine - 1 < 0 || errLine - 1 >= lines.length) {
            return "";
        }
        String lineText = lines[errLine - 1];
        if (errColumn > lineText.length()) {
            errColumn = lineText.length();
        }
        StringBuilder pointer = new StringBuilder();
        for (int i = 0; i < errColumn - 1 && i < lineText.length(); i++) {
            if (lineText.charAt(i) == '\t') {
                pointer.append("\t");
            } else {
                pointer.append("-");
            }
        }
        pointer.append("^");
        if (colored) {
            pointer.insert(0, "\033[31m").append("\033[0m");
        }
        return "    " + lineText + "\n    " + pointer.toString();
    }

    public static int[] getErrorPosition(Exception err) {
        if (err instanceof PositionalError) {
            PositionalError pe = (PositionalError) err;
            return new int[]{pe.getLine(), pe.getColumn()};
        }
        Pattern pattern = Pattern.compile("at line (\\d+), column (\\d+)");
        Matcher matcher = pattern.matcher(err.getMessage());
        if (matcher.find()) {
            try {
                int line = Integer.parseInt(matcher.group(1));
                int col = Integer.parseInt(matcher.group(2));
                return new int[]{line, col};
            } catch (NumberFormatException ex) {
                // ignore and fall through
            }
        }
        return new int[]{0, 0};
    }
}
// --------- FILE END: "Errors.java" ----------
