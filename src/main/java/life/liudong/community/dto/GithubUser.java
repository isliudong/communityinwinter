package life.liudong.community.dto;

import lombok.Data;

/**
 * @author liudong
 */
@Data
public class GithubUser {
    private String name;
    private Long id;
    private String bio;
    private String avatarUrl;
}
