package life.liudong.community.cache;

import life.liudong.community.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 发布问题标签，为了简便直接写入代码；后期考虑持久化
 * @author 闲乘月
 * @since 2020-02-16 21:27
 **/
public class TagCache {


    public static List<TagDTO> get() {
        ArrayList<TagDTO> tagDTOS = new ArrayList<>();
        TagDTO tagsTab1 = new TagDTO();
        tagsTab1.setCategoryName("开发语言");
        tagsTab1.setTag(Arrays.asList("javascript", "php", "css", "html", "html5", "java", "node.js", "python", "c++", "c", "golang", "objective-c", "typescript", "shell", "swift", "c#", "sass", "ruby", "bash", "less", "asp.net", "lua", "scala", "coffeescript", "actionscript", "rust", "erlang", "perl"));

        tagDTOS.add(tagsTab1);

        TagDTO tagsTab2 = new TagDTO();
        tagsTab2.setCategoryName("平台框架");
        tagsTab2.setTag(Arrays.asList("laravel", "spring", "express", "django", "flask", "yii", "ruby-on-rails", "tornado", "koa", "struts"));
        tagDTOS.add(tagsTab2);

        TagDTO tagsTab3 = new TagDTO();
        tagsTab3.setCategoryName("服务器");
        tagsTab3.setTag(Arrays.asList("linux", "nginx", "docker", "apache", "ubuntu", "centos", "缓存", "tomcat", "负载均衡", "unix", "hadoop", "windows-server"));
        tagDTOS.add(tagsTab3);

        TagDTO tagsTab4 = new TagDTO();
        tagsTab4.setCategoryName("数据库");
        tagsTab4.setTag(Arrays.asList("mysql", "redis", "mongodb", "sql", "oracle", "nosql", "memcached", "sqlserver", "postgresql", "sqlite"));
        tagDTOS.add(tagsTab4);

        TagDTO tagsTab5 = new TagDTO();
        tagsTab5.setCategoryName("流行工具");
        tagsTab5.setTag(Arrays.asList("git", "github", "visual-studio-code", "vim", "sublime-text", "xcode", "intellij-idea", "eclipse", "maven", "ide", "svn", "visual-studio", "atom", "emacs", "textmate", "hg"));
        tagDTOS.add(tagsTab5);

        TagDTO tagsTab6 = new TagDTO();
        tagsTab6.setCategoryName("其他");
        tagsTab6.setTag(Arrays.asList("广告", "求职", "招聘"));
        tagDTOS.add(tagsTab6);

        return tagDTOS;
    }

    /**
     * 返回校验不通过的标签
     */

    public static String filterInValid(String tagString) {
        String[] split = StringUtils.split(tagString, ",");

        List<TagDTO> tagDTOList = get();

        List<String> tagList = tagDTOList.stream().flatMap(tag -> tag.getTag().stream()).collect(Collectors.toList());
        return Arrays.stream(split).filter(t -> !tagList.contains(t)).collect(Collectors.joining(","));
    }
}
