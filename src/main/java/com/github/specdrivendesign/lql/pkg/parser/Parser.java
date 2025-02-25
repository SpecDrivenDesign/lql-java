// --------- FILE START: "Parser.java" (converted from pkg/parser/parser.go) ----------
package com.github.specdrivendesign.lql.pkg.parser;

import com.github.specdrivendesign.lql.pkg.ast.Expression;
import com.github.specdrivendesign.lql.pkg.ast.expressions.*;
import com.github.specdrivendesign.lql.pkg.errors.Errors;
import com.github.specdrivendesign.lql.pkg.lexer.Lexer;
import com.github.specdrivendesign.lql.pkg.tokens.Tokens;
import com.github.specdrivendesign.lql.pkg.types.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private Lexer lexer;
    private Tokens.Token curToken;
    private Tokens.Token peekToken;
    private List<String> errors;

    public Parser(Lexer lexer) throws Exception {
        this.lexer = lexer;
        this.errors = new ArrayList<>();
        nextToken();
        nextToken();
    }

    private void nextToken() throws Exception {
        curToken = peekToken;
        peekToken = lexer.nextToken();
    }

    public Expression parseExpression() throws Exception {
        return parseOrExpression();
    }

    private Expression parseOrExpression() throws Exception {
        Expression left = parseAndExpression();
        while (curToken.getType() == Tokens.TokenOr ||
               (curToken.getType() == Tokens.TokenIdent && curToken.getLiteral().toUpperCase().equals("OR"))) {
            Tokens.Token operator = curToken;
            nextToken();
            Expression right = parseAndExpression();
            left = new Binary(left, operator.getType(), right, operator.getLine(), operator.getColumn());
        }
        return left;
    }

    private Expression parseAndExpression() throws Exception {
        Expression left = parseEqualityExpression();
        while (curToken.getType() == Tokens.TokenAnd ||
               (curToken.getType() == Tokens.TokenIdent && curToken.getLiteral().toUpperCase().equals("AND"))) {
            Tokens.Token operator = curToken;
            nextToken();
            Expression right = parseEqualityExpression();
            left = new Binary(left, operator.getType(), right, operator.getLine(), operator.getColumn());
        }
        return left;
    }

    private Expression parseEqualityExpression() throws Exception {
        Expression left = parseRelationalExpression();
        while (curToken.getType() == Tokens.TokenEq || curToken.getType() == Tokens.TokenNeq) {
            Tokens.Token operator = curToken;
            nextToken();
            Expression right = parseRelationalExpression();
            left = new Binary(left, operator.getType(), right, operator.getLine(), operator.getColumn());
        }
        return left;
    }

    private Expression parseRelationalExpression() throws Exception {
        Expression left = parseAdditiveExpression();
        while (curToken.getType() == Tokens.TokenLt || curToken.getType() == Tokens.TokenGt ||
               curToken.getType() == Tokens.TokenLte || curToken.getType() == Tokens.TokenGte) {
            Tokens.Token operator = curToken;
            nextToken();
            Expression right = parseAdditiveExpression();
            left = new Binary(left, operator.getType(), right, operator.getLine(), operator.getColumn());
        }
        return left;
    }

    private Expression parseAdditiveExpression() throws Exception {
        Expression left = parseMultiplicativeExpression();
        while (curToken.getType() == Tokens.TokenPlus || curToken.getType() == Tokens.TokenMinus || curToken.getType() == Tokens.TokenModulo) {
            Tokens.Token operator = curToken;
            nextToken();
            Expression right = parseMultiplicativeExpression();
            left = new Binary(left, operator.getType(), right, operator.getLine(), operator.getColumn());
        }
        return left;
    }

    private Expression parseMultiplicativeExpression() throws Exception {
        Expression left = parseUnaryExpression();
        while (curToken.getType() == Tokens.TokenMultiply || curToken.getType() == Tokens.TokenDivide) {
            Tokens.Token operator = curToken;
            nextToken();
            Expression right = parseUnaryExpression();
            left = new Binary(left, operator.getType(), right, operator.getLine(), operator.getColumn());
        }
        return left;
    }

    private Expression parseUnaryExpression() throws Exception {
        if (curToken.getType() == Tokens.TokenNot || curToken.getType() == Tokens.TokenMinus) {
            Tokens.Token operator = curToken;
            nextToken();
            Expression expr = parseUnaryExpression();
            return new Unary(operator.getType(), expr, operator.getLine(), operator.getColumn());
        }
        return parseMemberAccessExpression();
    }

    private Expression parseMemberAccessExpression() throws Exception {
        Expression expr = parsePrimaryExpressionInner();
        while (curToken.getType() == Tokens.TokenDot ||
               curToken.getType() == Tokens.TokenLeftBracket ||
               curToken.getType() == Tokens.TokenQuestionDot ||
               curToken.getType() == Tokens.TokenQuestionBracket) {
            MemberAccess.MemberPart part;
            if (curToken.getType() == Tokens.TokenDot || curToken.getType() == Tokens.TokenQuestionDot) {
                boolean optional = curToken.getType() == Tokens.TokenQuestionDot;
                nextToken();
                if (curToken.getType() != Tokens.TokenIdent && curToken.getType() != Tokens.TokenString) {
                    throw Errors.newSyntaxError("Expected identifier after dot", curToken.getLine(), curToken.getColumn());
                }
                part = new MemberAccess.MemberPart(optional, false, curToken.getLiteral().trim(), null, curToken.getLine(), curToken.getColumn());
                nextToken();
            } else {
                boolean optional = curToken.getType() == Tokens.TokenQuestionBracket;
                nextToken();
                Expression indexExpr = parseExpression();
                if (curToken.getType() != Tokens.TokenRightBracket) {
                    throw Errors.newSyntaxError("Expected closing bracket", curToken.getLine(), curToken.getColumn());
                }
                nextToken();
                part = new MemberAccess.MemberPart(optional, true, null, indexExpr, curToken.getLine(), curToken.getColumn());
            }
            List<MemberAccess.MemberPart> parts = new ArrayList<>();
            parts.add(part);
            expr = new MemberAccess(expr, parts);
        }
        return expr;
    }

    private Expression parsePrimaryExpressionInner() throws Exception {
        switch (curToken.getType()) {
            case Tokens.TokenLparen:
                nextToken();
                Expression expr = parseExpression();
                if (curToken.getType() != Tokens.TokenRparen) {
                    throw Errors.newSyntaxError("Expected RPAREN", curToken.getLine(), curToken.getColumn());
                }
                nextToken();
                return expr;
            case Tokens.TokenNumber:
                Expression lit = new Literal(Types.parseNumber(curToken.getLiteral()), curToken.getLine(), curToken.getColumn());
                nextToken();
                return lit;
            case Tokens.TokenString:
                lit = new Literal(curToken.getLiteral(), curToken.getLine(), curToken.getColumn());
                nextToken();
                return lit;
            case Tokens.TokenBool:
                boolean b = curToken.getLiteral().equals("true");
                lit = new Literal(b, curToken.getLine(), curToken.getColumn());
                nextToken();
                return lit;
            case Tokens.TokenNull:
                lit = new Literal(null, curToken.getLine(), curToken.getColumn());
                nextToken();
                return lit;
            case Tokens.TokenDollar:
                return parseContextExpression();
            case Tokens.TokenLeftCurly:
                return parseObjectLiteral();
            case Tokens.TokenLeftBracket:
                return parseArrayLiteral();
            case Tokens.TokenIdent:
                if (peekToken.getType() == Tokens.TokenLparen || peekToken.getType() == Tokens.TokenDot) {
                    return parseFunctionCall();
                }
                throw Errors.newSyntaxError("Bare identifier '" + curToken.getLiteral() + "' is not allowed outside of context references or object keys", curToken.getLine(), curToken.getColumn());
            default:
                throw Errors.newSyntaxError("Unexpected token " + curToken.getLiteral(), curToken.getLine(), curToken.getColumn());
        }
    }

    private Expression parseContextExpression() throws Exception {
        Tokens.Token startToken = curToken;
        nextToken();
        if (curToken.getType() == Tokens.TokenIdent) {
            Identifier ident = new Identifier(curToken.getLiteral(), curToken.getLine(), curToken.getColumn());
            Expression ce = new Context(ident, null, startToken.getLine(), startToken.getColumn());
            nextToken();
            return ce;
        } else if (curToken.getType() == Tokens.TokenLeftBracket) {
            nextToken();
            Expression expr = parseExpression();
            if (curToken.getType() != Tokens.TokenRightBracket) {
                throw Errors.newSyntaxError("Expected RBRACKET in context expression", curToken.getLine(), curToken.getColumn());
            }
            nextToken();
            return new Context(null, expr, startToken.getLine(), startToken.getColumn());
        } else {
            return new Context(null, null, startToken.getLine(), startToken.getColumn());
        }
    }

    private Expression parseFunctionCall() throws Exception {
        List<String> parts = new ArrayList<>();
        parts.add(curToken.getLiteral());
        Tokens.Token startToken = curToken;
        nextToken();
        while (curToken.getType() == Tokens.TokenDot) {
            nextToken();
            if (curToken.getType() != Tokens.TokenIdent) {
                throw Errors.newSyntaxError("Expected identifier after dot in function call", curToken.getLine(), curToken.getColumn());
            }
            parts.add(curToken.getLiteral());
            nextToken();
        }
        if (curToken.getType() != Tokens.TokenLparen) {
            throw Errors.newSyntaxError("Expected '(' in function call", curToken.getLine(), curToken.getColumn());
        }
        Tokens.Token parenToken = curToken;
        nextToken();
        List<Expression> arguments = new ArrayList<>();
        if (curToken.getType() != Tokens.TokenRparen) {
            arguments.add(parseExpression());
            while (curToken.getType() == Tokens.TokenComma) {
                nextToken();
                arguments.add(parseExpression());
            }
            if (curToken.getType() != Tokens.TokenRparen) {
                throw Errors.newSyntaxError("Expected ')' after arguments in function call", curToken.getLine(), curToken.getColumn());
            }
        }
        nextToken();
        return new FunctionCall(parts, arguments, startToken.getLine(), startToken.getColumn(), parenToken.getLine(), parenToken.getColumn());
    }

    private Expression parseArrayLiteral() throws Exception {
        Tokens.Token startToken = curToken;
        List<Expression> elements = new ArrayList<>();
        nextToken();
        if (curToken.getType() == Tokens.TokenRightBracket) {
            nextToken();
            return new ArrayLiteral(elements, startToken.getLine(), startToken.getColumn());
        }
        elements.add(parseExpression());
        while (curToken.getType() == Tokens.TokenComma) {
            nextToken();
            elements.add(parseExpression());
        }
        if (curToken.getType() != Tokens.TokenRightBracket) {
            throw Errors.newSyntaxError("Expected ']' at end of array literal", curToken.getLine(), curToken.getColumn());
        }
        nextToken();
        return new ArrayLiteral(elements, startToken.getLine(), startToken.getColumn());
    }

    private Expression parseObjectLiteral() throws Exception {
        Tokens.Token startToken = curToken;
        Map<String, Expression> fields = new HashMap<>();
        nextToken();
        if (curToken.getType() == Tokens.TokenRightCurly) {
            nextToken();
            return new ObjectLiteral(fields, startToken.getLine(), startToken.getColumn());
        }
        while (true) {
            if (curToken.getType() != Tokens.TokenIdent && curToken.getType() != Tokens.TokenString) {
                throw Errors.newSyntaxError("Expected identifier or string as object key", curToken.getLine(), curToken.getColumn());
            }
            String key = curToken.getLiteral().trim();
            if (fields.containsKey(key)) {
                throw Errors.newSemanticError("Duplicate key '" + key + "' detected", curToken.getLine(), curToken.getColumn());
            }
            if (peekToken.getType() != Tokens.TokenColon) {
                throw Errors.newSyntaxError("Expected ':' after object key", peekToken.getLine(), peekToken.getColumn());
            }
            nextToken();
            nextToken();
            Expression valueExpr = parseExpression();
            fields.put(key, valueExpr);
            if (curToken.getType() == Tokens.TokenComma) {
                if (peekToken.getType() == Tokens.TokenRightCurly) {
                    throw Errors.newSyntaxError("Trailing comma not allowed in object literal", peekToken.getLine(), peekToken.getColumn());
                }
                nextToken();
            } else if (curToken.getType() == Tokens.TokenRightCurly) {
                break;
            } else {
                throw Errors.newSyntaxError("Expected ',' or '}' after object field", curToken.getLine(), curToken.getColumn());
            }
        }
        if (curToken.getType() != Tokens.TokenRightCurly) {
            throw Errors.newSyntaxError("Expected '}' at end of object literal", curToken.getLine(), curToken.getColumn());
        }
        nextToken();
        return new ObjectLiteral(fields, startToken.getLine(), startToken.getColumn());
    }
}
// --------- FILE END: "Parser.java" ----------
