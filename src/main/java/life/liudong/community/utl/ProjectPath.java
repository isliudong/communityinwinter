package life.liudong.community.utl;

/**
 * @author 28415@hand-china.com 2020/09/19 17:25
 */
public class ProjectPath {

    /**
     * 获取系统路径
     * @return project path
     */
    public static String  get(){
        return System.getProperty("user.dir")+"\\";
    }

    /**
     *
     * @return 项目图片位置
     */
    public static String getImgPath(){
        return System.getProperty("user.dir")+"\\src\\main\\resources\\static\\img\\";
    }
}
