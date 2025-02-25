package com.github.specdrivendesign.lql.pkg.lexer;

import com.github.specdrivendesign.lql.pkg.errors.Errors;
import com.github.specdrivendesign.lql.pkg.tokens.Tokens;
import com.github.specdrivendesign.lql.pkg.tokenstream.TokenStream;

import java.security.PrivateKey;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.Signature;

public class Lexer implements TokenStream {
    private String input;
    private int position;
    private int readPosition;
    private char ch;
    private int line;
    private int column;

    public Lexer(String input) {
        this.input = input;
        this.line = 1;
        this.column = 0;
        this.position = 0;
        this.readPosition = 0;
        readChar();
    }

    private void readChar() {
        if (readPosition >= input.length()) {
            ch = 0;
        } else {
            ch = input.charAt(readPosition);
        }
        position = readPosition;
        readPosition++;
        if (ch == '\n') {
            line++;
            column = 0;
        } else {
            column++;
        }
    }

    private char peekChar() {
        if (readPosition >= input.length()) {
            return 0;
        }
        return input.charAt(readPosition);
    }

    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void skipWhitespace() {
        while (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
            readChar();
        }
        // Skip comments starting with '#'
        while (ch == '#') {
            while (ch != '\n' && ch != 0) {
                readChar();
            }
            readChar();
            while (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
                readChar();
            }
        }
    }

    public Tokens.Token nextToken() throws Exception {
        skipWhitespace();
        int tokenLine = line;
        int tokenColumn = column;
        Tokens.Token token;
        switch (ch) {
            case '+':
                token = new Tokens.Token(Tokens.TokenPlus, String.valueOf(ch), tokenLine, tokenColumn);
                break;
            case '-':
                token = new Tokens.Token(Tokens.TokenMinus, String.valueOf(ch), tokenLine, tokenColumn);
                break;
            case '*':
                token = new Tokens.Token(Tokens.TokenMultiply, String.valueOf(ch), tokenLine, tokenColumn);
                break;
            case '/':
                token = new Tokens.Token(Tokens.TokenDivide, String.valueOf(ch), tokenLine, tokenColumn);
                break;
            case '%':
                token = new Tokens.Token(Tokens.TokenModulo, String.valueOf(ch), tokenLine, tokenColumn);
                break;
            case '<':
                if (peekChar() == '=') {
                    readChar();
                    token = new Tokens.Token(Tokens.TokenLte, "<=", tokenLine, tokenColumn);
                } else {
                    token = new Tokens.Token(Tokens.TokenLt, String.valueOf(ch), tokenLine, tokenColumn);
                }
                break;
            case '>':
                if (peekChar() == '=') {
                    readChar();
                    token = new Tokens.Token(Tokens.TokenGte, ">=", tokenLine, tokenColumn);
                } else {
                    token = new Tokens.Token(Tokens.TokenGt, String.valueOf(ch), tokenLine, tokenColumn);
                }
                break;
            case '=':
                if (peekChar() == '=') {
                    readChar();
                    token = new Tokens.Token(Tokens.TokenEq, "==", tokenLine, tokenColumn);
                } else {
                    token = new Tokens.Token(Tokens.TokenIllegal, String.valueOf(ch), tokenLine, tokenColumn);
                }
                break;
            case '!':
                if (peekChar() == '=') {
                    readChar();
                    token = new Tokens.Token(Tokens.TokenNeq, "!=", tokenLine, tokenColumn);
                } else {
                    token = new Tokens.Token(Tokens.TokenNot, String.valueOf(ch), tokenLine, tokenColumn);
                }
                break;
            case '&':
                if (peekChar() == '&') {
                    char prev = ch;
                    readChar();
                    token = new Tokens.Token(Tokens.TokenAnd, "" + prev + ch, tokenLine, tokenColumn);
                } else {
                    throw Errors.newLexicalError("Unexpected character: &", tokenLine, tokenColumn);
                }
                break;
            case '|':
                if (peekChar() == '|') {
                    char prev = ch;
                    readChar();
                    token = new Tokens.Token(Tokens.TokenOr, "" + prev + ch, tokenLine, tokenColumn);
                } else {
                    throw Errors.newLexicalError("Unexpected character: |", tokenLine, tokenColumn);
                }
                break;
            case '(':
                token = new Tokens.Token(Tokens.TokenLparen, String.valueOf(ch), tokenLine, tokenColumn);
                break;
            case ')':
                token = new Tokens.Token(Tokens.TokenRparen, String.valueOf(ch), tokenLine, tokenColumn);
                break;
            case '[':
                token = new Tokens.Token(Tokens.TokenLeftBracket, String.valueOf(ch), tokenLine, tokenColumn);
                break;
            case ']':
                token = new Tokens.Token(Tokens.TokenRightBracket, String.valueOf(ch), tokenLine, tokenColumn);
                break;
            case '{':
                token = new Tokens.Token(Tokens.TokenLeftCurly, String.valueOf(ch), tokenLine, tokenColumn);
                break;
            case '}':
                token = new Tokens.Token(Tokens.TokenRightCurly, String.valueOf(ch), tokenLine, tokenColumn);
                break;
            case ',':
                token = new Tokens.Token(Tokens.TokenComma, String.valueOf(ch), tokenLine, tokenColumn);
                break;
            case ':':
                token = new Tokens.Token(Tokens.TokenColon, String.valueOf(ch), tokenLine, tokenColumn);
                break;
            case '.':
                token = new Tokens.Token(Tokens.TokenDot, String.valueOf(ch), tokenLine, tokenColumn);
                break;
            case '?':
                if (peekChar() == '.') {
                    readChar();
                    token = new Tokens.Token(Tokens.TokenQuestionDot, "?.", tokenLine, tokenColumn);
                } else if (peekChar() == '[') {
                    readChar();
                    token = new Tokens.Token(Tokens.TokenQuestionBracket, "?[", tokenLine, tokenColumn);
                } else {
                    throw Errors.newLexicalError("Unexpected character: " + ch, tokenLine, tokenColumn);
                }
                break;
            case '$':
                token = new Tokens.Token(Tokens.TokenDollar, String.valueOf(ch), tokenLine, tokenColumn);
                break;
            case '"':
            case '\'':
                String str = readString(ch);
                token = new Tokens.Token(Tokens.TokenString, str, tokenLine, tokenColumn);
                return token;
            case 0:
                token = new Tokens.Token(Tokens.TokenEof, "", tokenLine, tokenColumn);
                break;
            default:
                if (isLetter(ch)) {
                    String ident = readIdentifier();
                    int type = lookupIdent(ident);
                    token = new Tokens.Token(type, ident, tokenLine, tokenColumn);
                    return token;
                } else if (isDigit(ch)) {
                    return readNumber();
                } else {
                    throw Errors.newLexicalError("Unexpected character: " + ch, tokenLine, tokenColumn);
                }
        }
        readChar();
        return token;
    }

    private String readIdentifier() {
        int start = position;
        while (isLetter(ch) || isDigit(ch)) {
            readChar();
        }
        return input.substring(start, position);
    }

    private int lookupIdent(String ident) {
        switch (ident) {
            case "true":
                return Tokens.TokenBool;
            case "false":
                return Tokens.TokenBool;
            case "null":
                return Tokens.TokenNull;
            case "AND":
                return Tokens.TokenAnd;
            case "OR":
                return Tokens.TokenOr;
            case "NOT":
                return Tokens.TokenNot;
            default:
                return Tokens.TokenIdent;
        }
    }

    private Tokens.Token readNumber() throws Exception {
        int start = position;
        int startLine = line;
        int startColumn = column;
        if (ch == '-' || ch == '+') {
            char sign = ch;
            readChar();
            if (!isDigit(ch)) {
                throw Errors.newLexicalError("Invalid number literal: '" + sign + "' not followed by a digit", startLine, startColumn);
            }
        }
        while (isDigit(ch)) {
            readChar();
        }
        if (ch == '.') {
            readChar();
            if (!isDigit(ch)) {
                throw Errors.newLexicalError("Invalid number literal: missing digits after decimal point", startLine, column);
            }
            while (isDigit(ch)) {
                readChar();
            }
        }
        if (ch == 'e' || ch == 'E') {
            readChar();
            if (ch == '-' || ch == '+') {
                readChar();
            }
            if (!isDigit(ch)) {
                throw Errors.newLexicalError("Invalid number literal: missing digits in exponent", startLine, startColumn);
            }
            while (isDigit(ch)) {
                readChar();
            }
        }
        String numStr = input.substring(start, position);
        return new Tokens.Token(Tokens.TokenNumber, numStr, startLine, startColumn);
    }

    private String readString(char quote) throws Exception {
        int startLine = line;
        int startColumn = column;
        StringBuilder sb = new StringBuilder();
        boolean escaped = false;
        readChar(); // skip opening quote
        while (ch != 0) {
            if (escaped) {
                if (ch == 'u') {
                    StringBuilder hexDigits = new StringBuilder();
                    for (int i = 0; i < 4; i++) {
                        readChar();
                        if (!isHexDigit(ch)) {
                            throw Errors.newLexicalError("Invalid unicode escape sequence", line, column);
                        }
                        hexDigits.append(ch);
                    }
                    int code = Integer.parseInt(hexDigits.toString(), 16);
                    sb.append((char) code);
                    escaped = false;
                } else {
                    switch (ch) {
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        case '\\': sb.append('\\'); break;
                        case '"': sb.append('"'); break;
                        case '\'': sb.append('\''); break;
                        default:
                            throw Errors.newLexicalError("Invalid escape sequence: \\" + ch, line, column);
                    }
                    escaped = false;
                }
            } else {
                if (ch == '\\') {
                    escaped = true;
                } else if (ch == quote) {
                    readChar();
                    return sb.toString();
                } else {
                    sb.append(ch);
                }
            }
            readChar();
        }
        throw Errors.newLexicalError("Unclosed string literal", startLine, startColumn);
    }

    private boolean isHexDigit(char c) {
        return (c >= '0' && c <= '9') ||
               (c >= 'a' && c <= 'f') ||
               (c >= 'A' && c <= 'F');
    }

    public byte[] exportTokens() throws Exception {
        List<Byte> byteList = new ArrayList<>();
        while (true) {
            Tokens.Token tok = nextToken();
            Byte code = Tokens.getTokenTypeToByte().get(tok.getType());
            if (code == null) {
                throw new Exception("unknown token type: " + tok.getType());
            }
            byteList.add(code);
            String fixed = Tokens.getFixedTokenLiteral(tok.getType());
            if (fixed.isEmpty() || !fixed.equals(tok.getLiteral())) {
                byte[] literalBytes = tok.getLiteral().getBytes();
                if (literalBytes.length > 255) {
                    throw new Exception("literal too long");
                }
                byteList.add((byte) literalBytes.length);
                for (byte b : literalBytes) {
                    byteList.add(b);
                }
            }
            if (tok.getType() == Tokens.TokenEof) {
                break;
            }
        }
        byte[] result = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            result[i] = byteList.get(i);
        }
        return result;
    }

    public byte[] exportTokensSigned(PrivateKey privateKey) throws Exception {
        byte[] tokenData = exportTokens();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(tokenData);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(tokenData);
        byte[] sigBytes = signature.sign();
        if (tokenData.length > 0xFFFFFFFFL) {
            throw new Exception("token data length exceeds maximum allowed size");
        }
        int tokenLen = tokenData.length;
        byte[] headerMagicBytes = Tokens.HeaderMagic.getBytes();
        int totalLength = headerMagicBytes.length + 4 + tokenLen + sigBytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(totalLength);
        buffer.put(headerMagicBytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(tokenLen);
        buffer.put(tokenData);
        buffer.put(sigBytes);
        return buffer.array();
    }

    public List<String> extractContextIdentifiers() throws Exception {
        List<String> identifiers = new ArrayList<>();
        while (true) {
            Tokens.Token tok = nextToken();
            if (tok.getType() == Tokens.TokenEof) break;
            if (tok.getType() == Tokens.TokenDollar) {
                Tokens.Token nextTok = nextToken();
                StringBuilder composed = new StringBuilder();
                while (nextTok.getType() != Tokens.TokenEof &&
                      (nextTok.getType() == Tokens.TokenDot ||
                       nextTok.getType() == Tokens.TokenQuestionDot ||
                       nextTok.getType() == Tokens.TokenQuestionBracket ||
                       nextTok.getType() == Tokens.TokenLeftBracket ||
                       nextTok.getType() == Tokens.TokenRightBracket ||
                       nextTok.getType() == Tokens.TokenIdent ||
                       nextTok.getType() == Tokens.TokenString ||
                       nextTok.getType() == Tokens.TokenNumber)) {
                    if (nextTok.getType() == Tokens.TokenIdent || nextTok.getType() == Tokens.TokenString) {
                        if (composed.length() > 0) {
                            composed.append(".");
                        }
                        composed.append(nextTok.getLiteral());
                    }
                    if (nextTok.getType() == Tokens.TokenNumber) {
                        composed.append(".*");
                    }
                    nextTok = nextToken();
                }
                if (composed.length() > 0) {
                    identifiers.add(composed.toString());
                }
            }
        }
        return identifiers;
    }

}
