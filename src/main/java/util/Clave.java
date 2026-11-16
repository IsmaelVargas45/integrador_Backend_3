package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Clave {

    private static final int ITERACIONES = 120000;
    private static final int BITS = 256;

    public static String hashear(String clave) {
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        return Base64.getEncoder().encodeToString(sal) + ":"
                + Base64.getEncoder().encodeToString(derivar(clave, sal));
    }

    public static boolean esHash(String guardada) {
        return guardada != null && guardada.contains(":");
    }

    public static boolean verificar(String clave, String guardada) {
        if (guardada == null) {
            return false;
        }
        try {
            if (!esHash(guardada)) { // contraseña antigua en texto plano
                return MessageDigest.isEqual(
                        clave.getBytes(StandardCharsets.UTF_8),
                        guardada.getBytes(StandardCharsets.UTF_8));
            }
            String[] partes = guardada.split(":");
            byte[] sal = Base64.getDecoder().decode(partes[0]);
            byte[] esperado = Base64.getDecoder().decode(partes[1]);
            return MessageDigest.isEqual(derivar(clave, sal), esperado);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] derivar(String clave, byte[] sal) {
        try {
            PBEKeySpec spec = new PBEKeySpec(clave.toCharArray(), sal, ITERACIONES, BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}