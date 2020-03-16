package life.liudong.community.dto;

/**
 * @program: community
 * @description: redis 测试专用
 * @author: 闲乘月
 * @create: 2020-03-16 14:47
 **/
import lombok.Data;

import java.io.Serializable;

@Data
public class redisTestDTO implements Serializable{

    private static final long serialVersionUID = 3002512957989050750L;
    private String name;
    private String description;
    private double price;
    private String cateLog;

    @Override
    public String toString() {

        return name+" "+description + " "+ cateLog+ " " + price;
    }
}