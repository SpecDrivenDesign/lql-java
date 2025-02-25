// --------- FILE START: "Bytecode.java" (converted from pkg/bytecode/bytecode.go) ----------
package com.github.specdrivendesign.lql.pkg.bytecode;

import com.github.specdrivendesign.lql.pkg.tokens.Tokens;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

public class Bytecode {

    public static class ByteCodeReader {
        private byte[] data;
        private int pos;

        public ByteCodeReader(byte[] data) {
            this.data = data;
            this.pos = 0;
        }

        public Tokens.Token nextToken() throws Exception {
            if (pos >= data.length) {
                return new Tokens.Token(Tokens.TokenEof, "", -1, -1);
            }
            // Read token type byte.
            byte tokenTypeByte = data[pos];
            pos++;
            Integer tokenType = byteToTokenType.get(tokenTypeByte);
            if (tokenType == null) {
                throw new Exception(String.format("unknown token type code: %d", tokenTypeByte));
            }
            String literal;
            // If the token has a fixed literal, use that.
            String fixed = Tokens.getFixedTokenLiteral(tokenType);
            if (!fixed.isEmpty()) {
                literal = fixed;
            } else {
                if (pos + 1 > data.length) {
                    throw new Exception("unexpected end of data reading literal length");
                }
                int length = data[pos] & 0xFF; // unsigned
                pos++;
                if (pos + length > data.length) {
                    throw new Exception("unexpected end of data reading literal");
                }
                literal = new String(data, pos, length);
                pos += length;
            }
            return new Tokens.Token(tokenType, literal, -1, -1);
        }
    }

    public static ByteCodeReader newByteCodeReader(byte[] data) {
        return new ByteCodeReader(data);
    }

    public static ByteCodeReader newByteCodeReaderFromSignedData(byte[] data, PublicKey pub) throws Exception {
        int sigSize = ((RSAPublicKey) pub).getModulus().bitLength() / 8;
        if (data.length < Tokens.HeaderMagic.length() + 4 + sigSize) {
            throw new Exception("data too short to contain valid signed tokens");
        }
        String headerMagic = new String(data, 0, Tokens.HeaderMagic.length());
        if (!headerMagic.equals(Tokens.HeaderMagic)) {
            throw new Exception(String.format("invalid header magic; expected %s", Tokens.HeaderMagic));
        }
        int pos = Tokens.HeaderMagic.length();
        if (pos + 4 > data.length) {
            throw new Exception("unexpected end of data reading length");
        }
        ByteBuffer bb = ByteBuffer.wrap(data, pos, 4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        int tokenDataLength = bb.getInt();
        pos += 4;
        int expectedLength = Tokens.HeaderMagic.length() + 4 + tokenDataLength + sigSize;
        if (data.length != expectedLength) {
            throw new Exception(String.format("data length mismatch: expected %d bytes, got %d", expectedLength, data.length));
        }
        byte[] tokenData = new byte[tokenDataLength];
        System.arraycopy(data, pos, tokenData, 0, tokenDataLength);
        pos += tokenDataLength;
        byte[] signature = new byte[sigSize];
        System.arraycopy(data, pos, signature, 0, sigSize);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(tokenData);
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(pub);
        sig.update(tokenData);
        if (!sig.verify(signature)) {
            throw new Exception("invalid signature");
        }
        return newByteCodeReader(tokenData);
    }

    private static final Map<Byte, Integer> byteToTokenType = new HashMap<>();

    static {
        // Reverse mapping: assume Tokens class provides a mapping from token type to byte.
        Map<Integer, Byte> tokenTypeToByte = Tokens.getTokenTypeToByte();
        for (Map.Entry<Integer, Byte> entry : tokenTypeToByte.entrySet()) {
            byteToTokenType.put(entry.getValue(), entry.getKey());
        }
    }
}
// --------- FILE END: "Bytecode.java" ----------
