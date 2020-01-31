package life.liudong.community.mapper;

import life.liudong.community.model.Question;

public interface QuestionExtMapper {
    int incView(Question record);
    int incCommentCount(Question record);
}