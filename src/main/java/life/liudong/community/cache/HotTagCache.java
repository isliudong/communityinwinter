package life.liudong.community.cache;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: community
 * @description: 热点标签缓存
 * @author: 闲乘月
 * @create: 2020-03-07 10:39
 **/
@Component
@Data
public class HotTagCache {

    private  Map<String,Integer> tags=new HashMap<>();

}
