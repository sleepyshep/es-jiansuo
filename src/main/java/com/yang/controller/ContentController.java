package com.yang.controller;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.yang.service.ContentService;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

// 请求编写
@RestController
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping("/parse/{index}")
    public Boolean parse(@PathVariable("index") String index) throws Exception {
        return contentService.parseContent(index);
    }

    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> search(@PathVariable("keyword") String keyword,
                                            @PathVariable("pageNo") int pageNo,
                                            @PathVariable("pageSize") int pageSize) throws IOException {

        return contentService.searchPage(keyword, pageNo, pageSize);
//        return contentService.searchHighLightPage(keyword, pageNo, pageSize);
    }

    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}/{grade}/{type}/{province}")
    public List<Map<String, Object>> search(@PathVariable("keyword") String keyword,
                                            @PathVariable("pageNo") int pageNo,
                                            @PathVariable("pageSize") int pageSize,
                                            @PathVariable("grade") String grade,
                                            @PathVariable("type") String type,
                                            @PathVariable("province") String province) throws IOException {

        return contentService.multiSearchPage(keyword, pageNo, pageSize, grade, type, province);
    }
}
