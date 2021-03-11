package io.github.linxiaobaixcg.serialize.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import io.github.linxiaobaixcg.serialize.Serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author lcq
 * @description: hessian序列化实现
 * @date 2021/3/4 14:50
 */
public class HessianImpl implements Serialize {
    @Override
    public <T> byte[] serialize(T o) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Hessian2Output ho = new Hessian2Output(os);
        try {
            ho.writeObject(o);
            ho.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ho.close();
            os.close();
        }
        return os.toByteArray();
    }

    @Override
    public <T> T deserialization(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Hessian2Input hi = new Hessian2Input(is);
        try {
            return (T) hi.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
