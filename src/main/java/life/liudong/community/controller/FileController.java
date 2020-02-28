package life.liudong.community.controller;

import life.liudong.community.dto.FileDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;

/**
 * @program: community
 * @description: 文件控制
 * @author: 闲乘月
 * @create: 2020-02-24 11:26
 **/
@Controller
public class FileController {
    @Value("${image.sever.url}")
    private String imgSever;


    @ResponseBody
    @RequestMapping("/file/upload")
    public FileDTO upload(HttpServletRequest request){

        MultipartHttpServletRequest multipartHttpServletRequest= (MultipartHttpServletRequest) request;
        MultipartFile file=multipartHttpServletRequest.getFile("editormd-image-file");



        //得到上传时的原文件名
        String originalFilename = file.getOriginalFilename();
        //获取文件后缀名
        String suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));
        //获取文件格式
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        //获取uuid作为文件名
        String name = UUID.randomUUID().toString().replaceAll("-", "");
        String saveName = name + suffixName;
        //虚拟存储服务器
        String picDir = imgSever;
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
        fileDTO.setUrl("/image_sever/"+saveName);
        return fileDTO;
    }
}
