package org.cataclysm.global.utils.serializer;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class Base64Utils {
    public static String encodeInstanceToBase64(Object object) throws Exception {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
        objStream.writeObject(object);
        objStream.flush();

        byte[] bytes = byteStream.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static Object decodeInstanceFromBase64(String base64) throws Exception {
        byte[] data = Base64.getDecoder().decode(base64);
        try (var byteStream = new java.io.ByteArrayInputStream(data);
             var objStream = new java.io.ObjectInputStream(byteStream)) {
            return objStream.readObject();
        }
    }
}
