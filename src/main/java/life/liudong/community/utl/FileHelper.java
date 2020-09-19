package life.liudong.community.utl;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import cn.hutool.core.util.IdUtil;

/**
 * @author 28415@hand-china.com 2020/09/19 17:44
 */
public class  FileHelper {

    public static  String downloadImg(String imgUrl) throws IOException {
        String name;
        name= IdUtil.simpleUUID() + ".jpg";
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
}
