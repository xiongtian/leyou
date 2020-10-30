package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author xiongtian
 * @create 2020/4/3-16:26
 */
@Service
public class UploadService {

    // 白名单
    private static final List<String> CONTENT_TYPE= Arrays.asList("image/gif","image/jpeg","image/png","multipart/form-data","application/json");

    // Log日志
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);


    @Autowired
    private FastFileStorageClient storageClient;

    public String uploadImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        //校验文件类型
        String contentType = file.getContentType();
        if (!CONTENT_TYPE.contains(contentType)) {

            // 记录不合法的文件
            LOGGER.info("文件类型不合法：{},{}",originalFilename,contentType);
            return null;
        }
        //校验文件的内容
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (null==bufferedImage ) {

                // 记录不合法文件
                LOGGER.info("文件内容不合法：{}"+originalFilename);
                return null;
            }
            //保存到服务器
            //file.transferTo(new File("E:\\IdeaWork\\heima49\\images"+filename));

            String ext = StringUtils.substringAfterLast(originalFilename, ".");
            StorePath storePath = this.storageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);



            //返回url进行回显    标记
            //return "http://image.leyou.com/"+originalFilename;
            return "http://image.leyou.com/"+storePath.getFullPath();
        } catch (IOException e) {
            LOGGER.info("服务器内部错误：{}",originalFilename);
            e.printStackTrace();
        }
        return null;
    }
}
