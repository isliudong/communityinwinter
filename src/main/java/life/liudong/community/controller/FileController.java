package life.liudong.community.controller;

import life.liudong.community.dto.FileDTO;
import life.liudong.community.exception.CustomizeErrorCode;
import life.liudong.community.exception.CustomizeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.UUID;


/**
 * @author liudong
 */
@Controller
public class FileController {
    @Value("${image.server.url}")
    private String imgServer;
    @Value("${web.upload-path}")
    private String fileServer;


    @ResponseBody
    @RequestMapping("/file/upload")
    public FileDTO upload(HttpServletRequest request) {

        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("editormd-image-file");


        //得到上传时的原文件名
        if (file == null) {
            throw new CustomizeException(CustomizeErrorCode.NULL_FILE);
        }

        String originalFilename = file.getOriginalFilename();
        //获取文件后缀名
        if (originalFilename == null) {
            throw new CustomizeException(CustomizeErrorCode.NO_FILE_NAME);
        }
        String suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));
        //获取uuid作为文件名
        String name = UUID.randomUUID().toString().replaceAll("-", "");
        String saveName = name + suffixName;
        //虚拟存储服务器
        String picDir = imgServer;
        //图片存储全路径
        String outputPath = picDir + saveName;

        try {
            File saveFile = new File(outputPath);
            file.transferTo(saveFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        FileDTO fileDTO = new FileDTO();
        fileDTO.setSuccess(1);
        fileDTO.setMessage("success");
        String url = imgServer.substring(fileServer.length()-1);
        fileDTO.setUrl(url + saveName);
        return fileDTO;
    }
}
