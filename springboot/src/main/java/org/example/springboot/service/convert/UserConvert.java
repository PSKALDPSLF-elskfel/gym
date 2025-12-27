package org.example.springboot.service.convert;

import org.example.springboot.dto.command.UserRegisterCommandDTO;
import org.example.springboot.dto.response.UserDetailResponseDTO;
import org.example.springboot.dto.response.UserLoginResponseDTO;

import org.example.springboot.entity.User;

import java.time.LocalDateTime;

/**
 * 用户转换类
 * @author system
 */
public class UserConvert {

    /**
     * 注册命令DTO转换为User实体
     * @param registerDTO 注册命令DTO
     * @param encodedPassword 加密后的密码
     * @return User实体
     */
    public static User registerCommandToEntity(UserRegisterCommandDTO registerDTO, String encodedPassword) {
        return User.builder()
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .password(encodedPassword)
                .nickname(registerDTO.getNickname())
                .phone(registerDTO.getPhone())
                .userType(registerDTO.getUserType())
                .memberType(0) // 默认为普通用户
                .status(1) // 1-正常
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * User实体转换为详情响应DTO
     * @param user User实体
     * @return 用户详情响应DTO
     */
    public static UserDetailResponseDTO entityToDetailResponse(User user) {
        return UserDetailResponseDTO.builder()
                .id(user.getId())
                .userType(user.getUserType())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .memberType(user.getMemberType())
                .memberExpireTime(user.getMemberExpireTime())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus())
                .userTypeDisplayName(user.getUserTypeDisplayName())
                .statusDisplayName(user.getStatusDisplayName())
                .memberTypeDisplayName(user.getMemberTypeDisplayName())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .build();
    }

    /**
     * 构建登录响应DTO
     * @param token JWT令牌
     * @param userInfo 用户信息
     * @return 登录响应DTO
     */
    public static UserLoginResponseDTO buildLoginResponse(String token, UserDetailResponseDTO userInfo) {
        return UserLoginResponseDTO.builder()
                .userInfo(userInfo)
                .token(token)
                .roleType(userInfo.getUserType())
                .build();
    }


}
