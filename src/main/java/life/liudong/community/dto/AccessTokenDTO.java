package life.liudong.community.dto;

import lombok.Data;

/**
 * @author liudong
 */
@Data
public class AccessTokenDTO {
    /**不可改为驼峰git接口要求*/
    private String client_id;
    private String client_secret;
    private String redirect_uri;
    private String code;
    private String state;
}
