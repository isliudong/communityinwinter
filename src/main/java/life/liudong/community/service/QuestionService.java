package life.liudong.community.service;

import life.liudong.community.dto.PaginationDTO;
import life.liudong.community.dto.QuestionDTO;
import life.liudong.community.mapper.QuestionMapper;
import life.liudong.community.mapper.UserMapper;
import life.liudong.community.model.Question;
import life.liudong.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;

    public PaginationDTO list(Integer page, Integer size) {
        Integer totalPage;
        Integer totalCount=questionMapper.count();
        if(totalCount%size==0){
            totalPage=totalCount/size;
        }
        else {totalPage=totalCount/size+1;}
        if (page<1)
            page=1;
        else if (page>totalPage)
            page=totalPage;
        Integer offset=size*(page-1);//分页起始数据位置

        List<Question> questionList=questionMapper.list(offset,size);
        List<QuestionDTO> questionDTOList=new ArrayList<>();
        for (Question question:questionList){
            User user=userMapper.findById(question.getCreator());
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

    public PaginationDTO list(Integer userId, Integer page, Integer size) {
        Integer totalPage;
        Integer totalCount=questionMapper.countByUserId(userId);
        if(totalCount%size==0){
            totalPage=totalCount/size;
        }
        else {totalPage=totalCount/size+1;}
        if (page<1)
            page=1;
        else if (page>totalPage)
            page=totalPage;
        Integer offset=size*(page-1);//分页参数
        List<Question> questionList=questionMapper.listByUserId(userId,offset,size);
        List<QuestionDTO> questionDTOList=new ArrayList<>();
        for (Question question:questionList){
            User user=userMapper.findById(question.getCreator());
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

    public QuestionDTO getById(Integer id) {
        Question question=questionMapper.getById(id);
        User user=userMapper.findById(question.getCreator());
        QuestionDTO questionDTO=new QuestionDTO();
        questionDTO.setUser(user);
        BeanUtils.copyProperties(question,questionDTO);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if (question.getId()==null)
        {
            question.setGmt_create(System.currentTimeMillis());
            question.setGmt_modified(question.getGmt_create());
            questionMapper.create(question);
        }
        else {
            question.setGmt_modified(System.currentTimeMillis());
            questionMapper.update(question);
        }
    }
}
