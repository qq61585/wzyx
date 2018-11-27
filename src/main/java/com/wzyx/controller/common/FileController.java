package com.wzyx.controller.common;

import com.wzyx.service.IFileService;
import com.wzyx.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Controller
public class FileController {


    @Autowired
    private IFileService fileService;

    @RequestMapping(value = "/image/{imageName}.{suffix}")
    public void getImage(@PathVariable(value = "imageName")String  imageName,
                         @PathVariable(value = "suffix") String suffix,
                         HttpServletResponse response) throws IOException {
        response.sendRedirect(PropertiesUtil.getProperty("ftp.server.ftp.prefix") + File.separator + imageName + "." + suffix);
    }

}
