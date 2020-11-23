package life.liudong.community.utl;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import cn.hutool.core.util.IdUtil;
import life.liudong.community.exception.CustomizeException;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 28415@hand-china.com 2020/09/19 17:44
 */
public class  FileHelper {

    public static  String downloadImg(String imgUrl) throws IOException {
        String name;
        name = randomImgName();
        FileOutputStream fileOutputStream = new FileOutputStream(new File(ProjectPath.getImgPath()+name));
        URL url = new URL(imgUrl);
        DataInputStream dataInputStream = new DataInputStream(url.openStream());

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024*5];

            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());

        } catch (IOException e) {
            System.out.println("图片下载失败");
            e.printStackTrace();
        }finally {
            dataInputStream.close();
            fileOutputStream.close();
        }
        return name;
    }

    /**
     * 随机图片名
     * @return name
     */
    public static String randomImgName() {
        return IdUtil.simpleUUID() + ".jpg";
    }

    /**
     * 保存头像
     * @param file MultipartFile
     * @return avatarUrl
     */
    public static String saveImg(MultipartFile file) {
        String imgName = randomImgName();
        File img = new File(ProjectPath.getImgPath() + imgName);
        try {
            file.transferTo(img);
        } catch (IOException e) {
            throw new CustomizeException("头像保存失败");
        }
        return "/img/"+imgName;
    }
}
