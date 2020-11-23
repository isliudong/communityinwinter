package life.liudong.community.service;

import com.alibaba.fastjson.JSON;
import life.liudong.community.cache.RedisOp;
import life.liudong.community.dto.PaginationDTO;
import life.liudong.community.dto.QuestionDTO;
import life.liudong.community.dto.QuestionQueryDTO;
import life.liudong.community.exception.CustomizeErrorCode;
import life.liudong.community.exception.CustomizeException;
import life.liudong.community.mapper.QuestionExtMapper;
import life.liudong.community.mapper.QuestionMapper;
import life.liudong.community.mapper.UserMapper;
import life.liudong.community.model.Question;
import life.liudong.community.model.QuestionExample;
import life.liudong.community.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author liudong
 */
@Slf4j
@Service
public class QuestionService {
    private final QuestionExtMapper questionExtMapper;
    private final UserMapper userMapper;
    private final QuestionMapper questionMapper;
    private final RedisOp<PaginationDTO<QuestionDTO>> redisOP;
    private final RestHighLevelClient restHighLevelClient;

    public QuestionService(QuestionExtMapper questionExtMapper, UserMapper userMapper, QuestionMapper questionMapper, RedisOp<PaginationDTO<QuestionDTO>> redisOp, RestHighLevelClient restHighLevelClient) {
        this.questionExtMapper = questionExtMapper;
        this.userMapper = userMapper;
        this.questionMapper = questionMapper;
        this.redisOP = redisOp;
        this.restHighLevelClient = restHighLevelClient;
    }

    public PaginationDTO<QuestionDTO> list(String search, String tag, Integer page, Integer size) {

        //标签为空
        if (StringUtils.isNotBlank(search)) {
            String[] tags = StringUtils.split(search, " ");
            search = String.join("|", tags);
        }else {
            search=null;
        }


        int totalPage;
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setTag(tag);
        questionQueryDTO.setSearch(search);
        //简单强转为int，用户不多
        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if (page < 1) {
            page = 1;
        }else if (page > totalPage&&totalPage>0) {
            page = totalPage;
        }
        //分页起始数据位置
        Integer offset = size * (page - 1);
        QuestionExample questionExample = new QuestionExample();
        //添加倒叙排列
        questionExample.setOrderByClause("gmt_create desc");
        questionQueryDTO.setPage(offset);
        questionQueryDTO.setSize(size);
        List<Question> questionList = questionExtMapper.selectBySearch(questionQueryDTO);


        return setPage(page, totalPage, questionList);
    }
    public PaginationDTO<QuestionDTO> quickList(String search, Integer page, Integer size) {

        //标签为空
        if (StringUtils.isNotBlank(search)) {
            String[] tags = StringUtils.split(search, " ");
            search = String.join("|", tags);
        }else {
            search=null;
        }
        int totalPage;
        List<Question> questions = quickSearch(page, size, search);
        //简单强转为int，用户不多
        Integer totalCount = questions.size();
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if (page < 1) {
            page = 1;
        }else if (page > totalPage&&totalPage>0) {
            page = totalPage;
        }

        return setPage(page, totalPage, questions);
    }

    public PaginationDTO<QuestionDTO> list(Long userId, Integer page, Integer size) {
        int totalPage;

        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount = (int) questionMapper.countByExample(questionExample);

        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if (page < 1) {
            page = 1;
        } else if (page > totalPage) {
            page = totalPage;
        }
        //分页参数
        int offset = size * (page - 1);
        QuestionExample questionExample1 = new QuestionExample();
        questionExample1.createCriteria().andCreatorEqualTo(userId);
        List<Question> questionList = questionMapper.selectByExampleWithRowbounds(questionExample1, new RowBounds(offset, size));

        return setPage(page, totalPage, questionList);
    }

    @NotNull
    private PaginationDTO<QuestionDTO> setPage(Integer page, int totalPage, List<Question> questionList) {
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questionList) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setData(questionDTOList);
        paginationDTO.setPagination(totalPage, page);
        return paginationDTO;
    }

    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setUser(user);
        BeanUtils.copyProperties(question, questionDTO);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        } else {

            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTag(question.getTag());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            QuestionExample questionExample = new QuestionExample();
            questionExample.createCriteria().andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion, questionExample);
            if (updated != 1) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
        addToEs(Collections.singletonList(question));
    }

    public void incView(Long id) {

        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);

    }

    public List<QuestionDTO> selectRelated(QuestionDTO questionDTO) {
        //标签为空
        if (StringUtils.isBlank(questionDTO.getTag())) {
            return new ArrayList<>();
        }

        String[] tags = StringUtils.split(questionDTO.getTag(), ",|，");
        Question question = new Question();
        question.setId(questionDTO.getId());

        //传入regex表达式
        question.setTag("^(" + String.join("|", tags) + ")$");
        List<Question> relatedQuestions = questionExtMapper.selectRelated(question);


        //将question转化为questionDTO
        return relatedQuestions.stream().map(q -> {
            QuestionDTO relatedQuestionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, relatedQuestionDTO);
            return relatedQuestionDTO;
        }).collect(Collectors.toList());
    }

    /**将paginationDTO写入redis缓存*/
    public void setPageInRedis(long id, PaginationDTO<QuestionDTO> paginationDTO) {
        try {
            redisOP.setObject(id, paginationDTO);
            log.info("==========>缓存首页刷新！");
        } catch (IOException e) {
           log.error("redis写入paginationDTO失败！详情:"+e.getMessage());
        }
    }

    /**在redis缓存获取paginationDTO*/
    public PaginationDTO<QuestionDTO> getPageByIdInRedis(long id) throws IOException, ClassNotFoundException {
        return redisOP.getObject(id);
    }

    public List<Question> getAll() {
        return questionMapper.selectByExample(new QuestionExample());
    }

    public List<Question> quickSearch(Integer page, Integer size, String keyword) {
        List<Map<String,Object>> list =new ArrayList<>();
        //构建搜索
        SearchRequest searchRequest = new SearchRequest("question");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //模糊匹配

        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("description", keyword);
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("description");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        //多高亮
        highlightBuilder.requireFieldMatch(true);
        sourceBuilder.query(matchQuery);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        sourceBuilder.from(page);
        sourceBuilder.highlighter(highlightBuilder);
        sourceBuilder.size(size);

        //执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse response;
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //解析,高亮字段替换原有字段
            for (SearchHit fields : response.getHits().getHits()) {
                Map<String, HighlightField> highlightFields = fields.getHighlightFields();
                HighlightField description = highlightFields.get("description");
                Map<String, Object> sourceAsMap = fields.getSourceAsMap();
                if (description!=null){
                    Text[] fragments = description.fragments();
                    StringBuilder description2= new StringBuilder();
                    for (Text fragment : fragments) {
                        description2.append(fragment);
                    }
                    sourceAsMap.put("description",description2);
                }
                list.add(sourceAsMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list.stream()
                .map((questionMap) -> JSON.parseObject(JSON.toJSONString(questionMap), Question.class))
                .collect(Collectors.toList());
    }



    /**
     * 初始化es索引库
     *
     */
    public void iniEs() throws IOException {
        addToEs(getAll());
    }
    public void addToEs(List<Question> questions) {
        //数据放入es
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");
        for (Question question : questions) {
            bulkRequest.add(
                    new IndexRequest("question")
                            .source(JSON.toJSONString(question), XContentType.JSON).type("ld")
            );
        }
        //执行请求
        BulkResponse bulk;
        try {
            bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            bulk.hasFailures();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
