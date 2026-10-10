package com.zl.book.controller;

import com.zl.book.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/file")
public class FileController {

    private static final Set<String> ALLOWED = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp", "bmp"
    ));

    @PostMapping("/upload")
    @ResponseBody
    public Response<String> upload(@RequestPart("file") MultipartFile multipartFile, HttpSession session) throws IOException {
        if (session.getAttribute("loginUser") == null) {
            return Response.error("请先登录");
        }
        if (multipartFile == null || multipartFile.isEmpty()) {
            return Response.error("请选择要上传的文件");
        }
        String originalFilename = multipartFile.getOriginalFilename();
        if (!StringUtils.hasLength(originalFilename) || !originalFilename.contains(".")) {
            return Response.error("文件名无效");
        }
        String fileType = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        if (!ALLOWED.contains(fileType)) {
            return Response.error("仅支持图片格式：jpg/png/gif/webp");
        }

        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + fileType;
        File dir = new File(System.getProperty("user.dir"), "file");
        if (!dir.exists() && !dir.mkdirs()) {
            return Response.error("创建上传目录失败");
        }
        File file = new File(dir, fileName);
        log.info("上传文件：{}", file.getAbsolutePath());
        multipartFile.transferTo(file.getAbsoluteFile());
        return Response.success("/file/" + fileName);
    }
}
