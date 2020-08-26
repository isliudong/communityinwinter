package life.liudong.community.cache;

import life.liudong.community.dto.HotTagDTO;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 热点标签缓存
 * @author 闲乘月
 * @since 2020-03-07 10:39
 **/

@Component
@Data
public class HotTagCache {

    private  Map<String,Integer> tags=new HashMap<>();
    private List<String> hots=new ArrayList<>();

    public void updateTags(Map<String,Integer> tags){
        //清空历史热点
        hots.clear();

        //使用优先队列遍历标签权重获取top 3标签（大顶堆）
        int max=8;
        PriorityQueue<HotTagDTO> priorityQueue=new PriorityQueue<>(max);

        tags.forEach((name,priority)->{
            HotTagDTO hotTagDTO = new HotTagDTO();
            hotTagDTO.setName(name);
            hotTagDTO.setPriority(priority);
            if (priorityQueue.size()<max){
                priorityQueue.add(hotTagDTO);
            }else {
                HotTagDTO minHot = priorityQueue.peek();
                if (hotTagDTO.compareTo(minHot)>0){
                    priorityQueue.poll();
                    priorityQueue.add(hotTagDTO);
                }
            }
        });

        //新热门标签放入hots并从大到小排序
        HotTagDTO poll = priorityQueue.poll();
        while (poll!=null){
            //将后出队列的标签（最热）放在第一个
            hots.add(0,poll.getName());
            poll=priorityQueue.poll();

        }

        System.out.println(hots);


    }

}
