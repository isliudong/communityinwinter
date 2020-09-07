package life.liudong.community.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.*;


/**
 * @author liudong
 */
@Component
public class RedisOp<T> {
    final
    RedisTemplate redisTemplate;

    public RedisOp(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setObject(Long id, T object) throws IOException {
        //将对象序列化成字节数组
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(ba);

        //用对象序列化流来将p对象序列化，然后把序列化之后的二进制数据写到ba流中
        oos.writeObject(object);

        //将ba流转成byte数组
        byte[] pBytes = ba.toByteArray();

        //将对象序列化之后的byte数组存到redis的string结构数据中
        redisTemplate.opsForValue().set(id, pBytes);
    }

    public T getObject(Long id) throws IOException, ClassNotFoundException {
        T object=null;
        //根据key从redis中取出对象的byte数据
        byte[] pBytesResp = (byte[]) redisTemplate.opsForValue().get(id);

        //将byte数据反序列出对象
        ByteArrayInputStream bi;
        if (pBytesResp!=null){
         bi= new ByteArrayInputStream(pBytesResp);
         ObjectInputStream oi = new ObjectInputStream(bi);
            //从对象读取流中读取出p对象
            object= (T) oi.readObject();
        }
        return object;
    }
}
