package life.liudong.community.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * @author liudong
 */
@Data
public class redisTestDTO implements Serializable{

    private static final long serialVersionUID = 3002512957989050750L;
    private String name;
    private String description;
    private Double price;
    private String cateLog;

}