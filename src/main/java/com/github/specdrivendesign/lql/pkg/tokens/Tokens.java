package com.github.specdrivendesign.lql.pkg.tokens;

import java.util.HashMap;
import java.util.Map;

public class Tokens {
    public static final String HeaderMagic = "STOK";

    // Token type constants.
    public static final int TokenEof             = 0;
    public static final int TokenIllegal         = 1;
    public static final int TokenIdent           = 2;
    public static final int TokenNumber          = 3;
    public static final int TokenString          = 4;
    public static final int TokenBool            = 5;
    public static final int TokenNull            = 6;
    public static final int TokenPlus            = 7;
    public static final int TokenMinus           = 8;
    public static final int TokenMultiply        = 9;
    public static final int TokenDivide          = 10;
    public static final int TokenLt              = 11;
    public static final int TokenGt              = 12;
    public static final int TokenLte             = 13;
    public static final int TokenGte             = 14;
    public static final int TokenEq              = 15;
    public static final int TokenNeq             = 16;
    public static final int TokenAnd             = 17;
    public static final int TokenOr              = 18;
    public static final int TokenNot             = 19;
    public static final int TokenLparen          = 20;
    public static final int TokenRparen          = 21;
    public static final int TokenLeftBracket     = 22;
    public static final int TokenRightBracket    = 23;
    public static final int TokenLeftCurly       = 24;
    public static final int TokenRightCurly      = 25;
    public static final int TokenComma           = 26;
    public static final int TokenColon           = 27;
    public static final int TokenDot             = 28;
    public static final int TokenQuestion        = 29;
    public static final int TokenQuestionDot     = 30;
    public static final int TokenQuestionBracket = 31;
    public static final int TokenDollar          = 32;
    public static final int TokenModulo          = 33;

    private static final Map<Integer, String> fixedTokenLiterals = new HashMap<>();
    private static final Map<Integer, Byte> tokenTypeToByte = new HashMap<>();

    static {
        fixedTokenLiterals.put(TokenPlus, "+");
        fixedTokenLiterals.put(TokenMinus, "-");
        fixedTokenLiterals.put(TokenMultiply, "*");
        fixedTokenLiterals.put(TokenDivide, "/");
        fixedTokenLiterals.put(TokenLt, "<");
        fixedTokenLiterals.put(TokenGt, ">");
        fixedTokenLiterals.put(TokenLte, "<=");
        fixedTokenLiterals.put(TokenGte, ">=");
        fixedTokenLiterals.put(TokenEq, "==");
        fixedTokenLiterals.put(TokenNeq, "!=");
        fixedTokenLiterals.put(TokenAnd, "AND");
        fixedTokenLiterals.put(TokenOr, "OR");
        fixedTokenLiterals.put(TokenNot, "NOT");
        fixedTokenLiterals.put(TokenLparen, "(");
        fixedTokenLiterals.put(TokenRparen, ")");
        fixedTokenLiterals.put(TokenLeftBracket, "[");
        fixedTokenLiterals.put(TokenRightBracket, "]");
        fixedTokenLiterals.put(TokenLeftCurly, "{");
        fixedTokenLiterals.put(TokenRightCurly, "}");
        fixedTokenLiterals.put(TokenComma, ",");
        fixedTokenLiterals.put(TokenColon, ":");
        fixedTokenLiterals.put(TokenDot, ".");
        fixedTokenLiterals.put(TokenQuestionDot, "?.");
        fixedTokenLiterals.put(TokenQuestionBracket, "?[");
        fixedTokenLiterals.put(TokenDollar, "$");
        fixedTokenLiterals.put(TokenModulo, "%");

        // Simple mapping from token type to a unique byte code.
        tokenTypeToByte.put(TokenEof, (byte) 0);
        tokenTypeToByte.put(TokenIllegal, (byte) 1);
        tokenTypeToByte.put(TokenIdent, (byte) 2);
        tokenTypeToByte.put(TokenNumber, (byte) 3);
        tokenTypeToByte.put(TokenString, (byte) 4);
        tokenTypeToByte.put(TokenBool, (byte) 5);
        tokenTypeToByte.put(TokenNull, (byte) 6);
        tokenTypeToByte.put(TokenPlus, (byte) 7);
        tokenTypeToByte.put(TokenMinus, (byte) 8);
        tokenTypeToByte.put(TokenMultiply, (byte) 9);
        tokenTypeToByte.put(TokenDivide, (byte) 10);
        tokenTypeToByte.put(TokenLt, (byte) 11);
        tokenTypeToByte.put(TokenGt, (byte) 12);
        tokenTypeToByte.put(TokenLte, (byte) 13);
        tokenTypeToByte.put(TokenGte, (byte) 14);
        tokenTypeToByte.put(TokenEq, (byte) 15);
        tokenTypeToByte.put(TokenNeq, (byte) 16);
        tokenTypeToByte.put(TokenAnd, (byte) 17);
        tokenTypeToByte.put(TokenOr, (byte) 18);
        tokenTypeToByte.put(TokenNot, (byte) 19);
        tokenTypeToByte.put(TokenLparen, (byte) 20);
        tokenTypeToByte.put(TokenRparen, (byte) 21);
        tokenTypeToByte.put(TokenLeftBracket, (byte) 22);
        tokenTypeToByte.put(TokenRightBracket, (byte) 23);
        tokenTypeToByte.put(TokenLeftCurly, (byte) 24);
        tokenTypeToByte.put(TokenRightCurly, (byte) 25);
        tokenTypeToByte.put(TokenComma, (byte) 26);
        tokenTypeToByte.put(TokenColon, (byte) 27);
        tokenTypeToByte.put(TokenDot, (byte) 28);
        tokenTypeToByte.put(TokenQuestion, (byte) 29);
        tokenTypeToByte.put(TokenQuestionDot, (byte) 30);
        tokenTypeToByte.put(TokenQuestionBracket, (byte) 31);
        tokenTypeToByte.put(TokenDollar, (byte) 32);
        tokenTypeToByte.put(TokenModulo, (byte) 33);
    }

    public static String getFixedTokenLiteral(int tokenType) {
        return fixedTokenLiterals.getOrDefault(tokenType, "");
    }

    public static Map<Integer, Byte> getTokenTypeToByte() {
        return tokenTypeToByte;
    }

    // The Token class used by the lexer and parser.
    public static class Token {
        private int type;
        private String literal;
        private int line;
        private int column;

        public Token(int type, String literal, int line, int column) {
            this.type = type;
            this.literal = literal;
            this.line = line;
            this.column = column;
        }

        public int getType() {
            return type;
        }

        public String getLiteral() {
            return literal;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }
    }
}
