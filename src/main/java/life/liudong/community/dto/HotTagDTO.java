package life.liudong.community.dto;

import lombok.Data;

/**
 * @program: community
 * @description:
 * @author: 闲乘月
 * @create: 2020-03-07 19:10
 **/
@Data
public class HotTagDTO implements Comparable {
    private String name;
    private Integer priority;

    @Override
    public int compareTo(Object o) {
        return this.getPriority()-((HotTagDTO)o).getPriority();
    }
}
