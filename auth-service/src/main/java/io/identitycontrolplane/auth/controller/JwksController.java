package io.identitycontrolplane.auth.controller;

import io.identitycontrolplane.auth.security.JwtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
public class JwksController {

    private final JwtUtil jwtUtil;

    public JwksController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> jwks() {
        RSAPublicKey pub = jwtUtil.getPublicKey();

        // BigInteger.toByteArray() uses two's complement — may prepend a 0x00 sign byte
        // for positive numbers whose top bit is 1. RFC 7518 requires unsigned big-endian.
        byte[] modulusBytes = pub.getModulus().toByteArray();
        if (modulusBytes[0] == 0) {
            modulusBytes = Arrays.copyOfRange(modulusBytes, 1, modulusBytes.length);
        }
        byte[] exponentBytes = pub.getPublicExponent().toByteArray();

        String n = Base64.getUrlEncoder().withoutPadding().encodeToString(modulusBytes);
        String e = Base64.getUrlEncoder().withoutPadding().encodeToString(exponentBytes);

        Map<String, Object> jwk = Map.of(
                "kty", "RSA",
                "use", "sig",
                "alg", "RS256",
                "kid", jwtUtil.getKeyId(),
                "n",   n,
                "e",   e
        );

        return Map.of("keys", List.of(jwk));
    }
}
