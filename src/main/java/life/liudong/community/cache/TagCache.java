package life.liudong.community.cache;

import life.liudong.community.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: community
 * @description: 发布问题标签，为了简便直接写入代码；后期考虑持久化
 * @author: 闲乘月
 * @create: 2020-02-16 21:27
 **/

public class TagCache {


    public static List<TagDTO> get() {
        ArrayList<TagDTO> tagDTOS = new ArrayList<>();
        TagDTO tagsTab1 = new TagDTO();
        tagsTab1.setCategoryName("开发语言1");
        tagsTab1.setTag(Arrays.asList("java1", "php", "js", "php", "js", "php", "js", "php", "js", "php", "js", "php", "js", "php", "js", "php", "js", "php", "js", "php", "js", "php", "js", "php", "js", "php", "js", "php", "js"));
        tagDTOS.add(tagsTab1);

        TagDTO tagsTab2 = new TagDTO();
        tagsTab2.setCategoryName("开发语言2");
        tagsTab2.setTag(Arrays.asList("java2", "php", "js"));
        tagDTOS.add(tagsTab2);

        TagDTO tagsTab3 = new TagDTO();
        tagsTab3.setCategoryName("开发语言3");
        tagsTab3.setTag(Arrays.asList("java3", "php", "js"));
        tagDTOS.add(tagsTab3);

        TagDTO tagsTab4 = new TagDTO();
        tagsTab4.setCategoryName("开发语言4");
        tagsTab4.setTag(Arrays.asList("java4", "php", "js"));
        tagDTOS.add(tagsTab4);
        return tagDTOS;
    }

    //返回校验不通过的标签***
    public static String filterInValid(String tagString) {
        String[] split = StringUtils.split(tagString, ",");

        List<TagDTO> tagDTOS = get();

        List<String> tagList = tagDTOS.stream().flatMap(tag -> tag.getTag().stream()).collect(Collectors.toList());
        String invalid = Arrays.stream(split).filter(t -> !tagList.contains(t)).collect(Collectors.joining(","));
        return invalid;
    }
}
