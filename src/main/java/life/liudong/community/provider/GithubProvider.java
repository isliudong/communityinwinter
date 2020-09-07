package life.liudong.community.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import life.liudong.community.dto.AccessTokenDTO;
import life.liudong.community.dto.GithubUser;
import life.liudong.community.exception.CustomizeErrorCode;
import life.liudong.community.exception.CustomizeException;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author liudong
 */
@Component
public class GithubProvider {
    public String getAccessToken(AccessTokenDTO accessTokenDTO) {

        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        String accessToken;
        String token;
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                accessToken = response.body().string();
                token = stringHand(accessToken);
                return token;
            } else {
                throw new CustomizeException(CustomizeErrorCode.ERROR_TO_LOGIN);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomizeException(CustomizeErrorCode.ERROR_TO_LOGIN);
        }
    }

    /**
     * 通过accessToken获取git用户信息
     */
    public GithubUser getUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://api.github.com/user")
                .header("Authorization", "token " + accessToken)
                .get().build();
        try {
            Response response = client.newCall(request).execute();
            assert response.body() != null;
            String responseJson = response.body().string();
            //通过JSON生成用户对象
            GithubUser githubUser = JSON.parseObject(responseJson, GithubUser.class);
            if (githubUser.getName() == null) {
                JSONObject jsonObject = JSON.parseObject(responseJson);
                githubUser.setName(jsonObject.getString("login"));
            }
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * token字符串处理
     *
     * @return token串
     */
    public String stringHand(String string) {
        String[] split = string.split("&");
        String token = split[0];
        return token.split("=")[1];
    }
}
