package life.liudong.community.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;


/**
 * @author liudong
 */
@Data
public class HotTagDTO implements Comparable<HotTagDTO> {
    private String name;
    private Integer priority;

    @Override
    public int compareTo(@NotNull HotTagDTO hotTagDTO) {
        return this.getPriority()-hotTagDTO.getPriority();
    }
}
