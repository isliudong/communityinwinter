package life.liudong.community.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import life.liudong.community.dto.AccessTokenDTO;
import life.liudong.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {
    public String getAccessToken(AccessTokenDTO accessTokenDTO){

        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String access_token=response.body().string();
            String token=StringHand(access_token);
            return token;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token="+accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseJsonn=response.body().string();
            GithubUser githubUser = JSON.parseObject(responseJsonn, GithubUser.class);//通过JSON生成用户对象
            if(githubUser.getName()==null)
            {
                JSONObject jsonObject=JSON.parseObject(responseJsonn);
                githubUser.setName(jsonObject.getString("login"));
            }
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //token字符串处理
    public   String StringHand(String string)
    {
        String[] split=string.split("&");
        String token1=split[0];
        String token2=token1.split("=")[1];
        return token2;
    }
}
