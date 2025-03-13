package de.uol.swp.common;

import java.io.*;

public class SerializationTestHelper {

    public static <T extends Serializable> byte[] pickle(T obj)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        return baos.toByteArray();
    }

    public static <T extends Serializable> T unpickle(byte[] b, Class<T> cl)
            throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object o = ois.readObject();
        return cl.cast(o);
    }

    public static <T extends Serializable> boolean checkSerializableAndDeserializable(T obj, Class<T> cl) {
        try {
            byte[] bytes = pickle(obj);
            T obj2 = unpickle(bytes, cl);
            return obj.equals(obj2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
