package life.liudong.community.service;

import life.liudong.community.dto.PaginationDTO;
import life.liudong.community.dto.QuestionDTO;
import life.liudong.community.exception.CustomizeErrorCode;
import life.liudong.community.exception.CustormizeException;
import life.liudong.community.mapper.QuestionExtMapper;
import life.liudong.community.mapper.QuestionMapper;
import life.liudong.community.mapper.UserMapper;
import life.liudong.community.model.Question;
import life.liudong.community.model.QuestionExample;
import life.liudong.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionExtMapper questionExtMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;

    public PaginationDTO list(Integer page, Integer size) {
        Integer totalPage;
        Integer totalCount=(int)questionMapper.countByExample(new QuestionExample());//简单强转为int，用户不多
        if(totalCount%size==0){
            totalPage=totalCount/size;
        }
        else {totalPage=totalCount/size+1;}
        if (page<1)
            page=1;
        else if (page>totalPage)
            page=totalPage;
        Integer offset=size*(page-1);//分页起始数据位置

        List<Question> questionList = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, size));


        List<QuestionDTO> questionDTOList=new ArrayList<>();
        for (Question question:questionList){
            User user=userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);//快速属性copy
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        PaginationDTO paginationDTO=new PaginationDTO();
        paginationDTO.setQuestionList(questionDTOList);

        paginationDTO.setPagination(totalPage,page);

        return paginationDTO;
    }

    public PaginationDTO list(Long userId, Integer page, Integer size) {
        Integer totalPage;

        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount=(int)questionMapper.countByExample(questionExample);

        if(totalCount%size==0){
            totalPage=totalCount/size;
        }
        else {totalPage=totalCount/size+1;}
        if (page<1)
            page=1;
        else if (page>totalPage)
            page=totalPage;
        Integer offset=size*(page-1);//分页参数
        //List<Question> questionList=questionMapper.listByUserId(userId,offset,size);
        QuestionExample questionExample1 = new QuestionExample();
        questionExample1.createCriteria().andCreatorEqualTo(userId);
        List<Question> questionList = questionMapper.selectByExampleWithRowbounds(questionExample1, new RowBounds(offset, size));

        List<QuestionDTO> questionDTOList=new ArrayList<>();
        for (Question question:questionList){
            User user=userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);//快速属性copy
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        PaginationDTO paginationDTO=new PaginationDTO();
        paginationDTO.setQuestionList(questionDTOList);



        paginationDTO.setPagination(totalPage,page);


        return paginationDTO;
    }

    public QuestionDTO getById(Long id) {
        Question question=questionMapper.selectByPrimaryKey(id);
        if (question==null) {
            throw new CustormizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        User user=userMapper.selectByPrimaryKey(question.getCreator());
        QuestionDTO questionDTO=new QuestionDTO();
        questionDTO.setUser(user);
        BeanUtils.copyProperties(question,questionDTO);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if (question.getId()==null)
        {
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        }
        else {

            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTag(question.getTag());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            QuestionExample questionExample = new QuestionExample();
            questionExample.createCriteria().andIdEqualTo(question.getId());
            int updated=questionMapper.updateByExampleSelective(updateQuestion, questionExample);
            if (updated!=1){
                throw new CustormizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void incView(Long id) {

        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);

    }
}
