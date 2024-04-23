package com.zmy.controller;

import com.zmy.common.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zmy
 * @create 2024-04-23 9:39
 */
@RestController
@RequestMapping("resource")
@Tag(name="资源模块")
@Slf4j
public class ResourceController {
    @Operation(summary="资源列表")
    @GetMapping("list")
    public ResponseResult<String> getResourceList(){
        log.debug("debug获取资源列表");
        log.info("info获取资源列表");
        return ResponseResult.ok("获取资源列表成功");
    }
}
