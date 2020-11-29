package life.liudong.community.service;

import com.alibaba.fastjson.JSON;
import life.liudong.community.exception.CustomizeErrorCode;
import life.liudong.community.exception.CustomizeException;
import life.liudong.community.model.Question;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author 28415@hand-china.com 2020/11/29 11:56
 */
@Service
@Slf4j
public class EsService {
    private final RestHighLevelClient restHighLevelClient;

    public EsService(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Async("communityPool")
    public void  addToEs(List<Question> questions) {
        //数据放入es
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");
        for (Question question : questions) {
            bulkRequest.add(
                    new IndexRequest("com-doc")
                            .source(JSON.toJSONString(question), XContentType.JSON)
                            .type("doc")
                            .id(question.getId().toString())
            );
            log.info("准备更新问题：id->{}到es------",question.getId());
        }
        //执行请求
        BulkResponse bulk;

        try {
            bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (BooleanUtils.isNotTrue(bulk.hasFailures())) {
                log.info("更新成功");
            }else {
                log.info("更新失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomizeException(CustomizeErrorCode.SYS_ERROR);
        }
    }
}
