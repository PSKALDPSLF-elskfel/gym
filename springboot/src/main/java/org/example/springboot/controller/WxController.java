package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.springboot.common.Result;
import org.example.springboot.dto.command.WxLoginDTO;
import org.example.springboot.dto.response.UserLoginResponseDTO;
import org.example.springboot.service.WxLoginService;
import org.springframework.web.bind.annotation.*;

/**
 * 微信登录Controller
 * @author system
 */
@RestController
@RequestMapping("/api/wx")
@Tag(name = "微信小程序", description = "微信小程序相关接口")
public class WxController {

    @Resource
    private WxLoginService wxLoginService;

    @PostMapping("/login")
    @Operation(summary = "微信小程序登录")
    public Result<UserLoginResponseDTO> wxLogin(@Valid @RequestBody WxLoginDTO loginDTO) {
        return Result.success(wxLoginService.wxLogin(loginDTO));
    }

    @PostMapping("/bind-phone")
    @Operation(summary = "绑定手机号")
    public Result<Void> bindPhone(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "手机号") @RequestParam String phone) {
        wxLoginService.bindPhone(userId, phone);
        return Result.success();
    }
}
