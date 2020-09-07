package life.liudong.community.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * @author liudong
 */
@Data
public class PaginationDTO<T> implements Serializable {
    private List<T> data;
    private Boolean showPrevious;
    private Boolean showFirstPage;
    private Boolean showNext;
    private Boolean showEndPage;
    private Integer page;
    /**包含页码*/
    private List<Integer> pages=new ArrayList<>();
    private Integer totalPage;


    /**未对page参数进行合法判断*/
    public void setPagination(Integer totalPage, Integer page) {
        this.totalPage=totalPage;
        //Integer totalPage;//页数
        int temp;//当前包含页码
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
                if(temp<totalPage) {
                    i--;
                }
                page++;
            }
        }

        //是否暂展示上一页按钮
        showPrevious= page != 1;
        //是否暂展示下一页按钮
        showNext= !page.equals(totalPage);
        //是否暂展示第一页按钮
        showFirstPage= !pages.contains(1);
        //是否暂展示最后一页按钮
        showEndPage= !pages.contains(totalPage);
    }
}
