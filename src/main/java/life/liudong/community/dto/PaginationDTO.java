package life.liudong.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class PaginationDTO {
    private List<QuestionDTO> questionList;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    private Integer page;//当前页码
    private List<Integer> pages=new ArrayList<>();//包含页码
    private Integer totalPage;


    //未对page参数进行合法判断
    public void setPagination(Integer totalPage, Integer page) {
        this.totalPage=totalPage;
        //Integer totalPage;//页数
        Integer temp;//当前包含页码
        this.page=page;

        //当前页包含页码设置
        for(int i=-3;i<4;i++)
        {
            temp=page+i;
            if (temp>0&&temp<=totalPage)
            {

                pages.add(temp);
            }
            else {
                if(temp<totalPage)
                i--;
                page++;
            }
        }

        //是否暂展示上一页按钮
        if(page==1){
            showPrevious=false;
        }
        else {showPrevious=true;}
        //是否暂展示下一页按钮
        if (page==totalPage){
            showNext=false;
        }
        else {showNext=true;}
        //是否暂展示第一页按钮
        if(pages.contains(1)){
            showFirstPage=false;
        }
        else {showFirstPage=true;}
        //是否暂展示最后一页按钮
        if(pages.contains(totalPage)){
            showEndPage=false;
        }
        else {showEndPage=true;}
    }
}
