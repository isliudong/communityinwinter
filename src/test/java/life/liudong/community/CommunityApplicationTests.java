package life.liudong.community;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import life.liudong.community.utl.FileHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommunityApplicationTests {

    @Test
    void contextLoads() throws IOException {

        String path = System.getProperty("user.dir")+"\\src\\main\\resources\\static\\img";
        System.out.println(path);

        System.out.println(FileHelper.downloadImg("https://avatars0.githubusercontent.com/u/53169912?v=4"));

    }

    //文件流操作
    @Test
    void test1() throws IOException {
        //读文件到数组
        File file = new File("E:/test.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] inputCache = new byte[(int) file.length()];
        int read = fileInputStream.read(inputCache);
        fileInputStream.close();
        System.out.println("读入完成   "+read);

        //从数组写到文件
        File target = new File("E:/test2.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(target, true);
        fileOutputStream.write(inputCache);
        fileOutputStream.close();

    }

}
