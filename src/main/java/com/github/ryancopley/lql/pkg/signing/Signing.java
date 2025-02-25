// --------- FILE START: "Signing.java" (converted from pkg/signing/signing.go) ----------
package com.github.ryancopley.lql.pkg.signing;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.*;
import java.util.Base64;

public class Signing {

    public static PrivateKey loadPrivateKey(String filename) throws Exception {
        File file = new File(filename);
        if (!filename.endsWith(".pem")) {
            throw new Exception("invalid private key file: expected a .pem file");
        }
        byte[] keyBytes = Files.readAllBytes(file.toPath());
        String keyString = new String(keyBytes);
        keyString = keyString.replace("-----BEGIN RSA PRIVATE KEY-----", "")
                             .replace("-----END RSA PRIVATE KEY-----", "")
                             .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(keyString);
        // Assuming the key is in PKCS#8 format
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public static PublicKey loadPublicKey(String filename) throws Exception {
        File file = new File(filename);
        if (!filename.endsWith(".pem")) {
            throw new Exception("invalid public key file: expected a .pem file");
        }
        byte[] keyBytes = Files.readAllBytes(file.toPath());
        String keyString = new String(keyBytes);
        keyString = keyString.replace("-----BEGIN RSA PUBLIC KEY-----", "")
                             .replace("-----END RSA PUBLIC KEY-----", "")
                             .replace("-----BEGIN PUBLIC KEY-----", "")
                             .replace("-----END PUBLIC KEY-----", "")
                             .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(keyString);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}
// --------- FILE END: "Signing.java" ----------
