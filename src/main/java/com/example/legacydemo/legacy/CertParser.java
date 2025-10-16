package com.example.legacydemo.legacy;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Demonstrates javax.security.cert, which is removed in Java 17.
 * In the upgrade, switch to java.security.cert.X509Certificate with a CertificateFactory.
 */
public class CertParser {
  public static String tryParsePem(String pem) {
    try {
      String body = pem.replace("-----BEGIN CERTIFICATE-----", "").replace("-----END CERTIFICATE-----", "").replaceAll("\\s", "");
      byte[] der = Base64.getDecoder().decode(body);
      // Using deprecated API intentionally
      javax.security.cert.X509Certificate c = javax.security.cert.X509Certificate.getInstance(der);
      return c.getSubjectDN().toString();
    } catch (Throwable t) {
      return "ERROR:" + t.getClass().getSimpleName();
    }
  }
}
