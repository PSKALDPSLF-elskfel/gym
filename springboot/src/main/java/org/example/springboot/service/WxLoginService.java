package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.dto.command.WxLoginDTO;
import org.example.springboot.dto.response.UserDetailResponseDTO;
import org.example.springboot.dto.response.UserLoginResponseDTO;
import org.example.springboot.entity.User;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.mapper.UserMapper;
import org.example.springboot.service.convert.UserConvert;
import org.example.springboot.util.JwtTokenUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

/**
 * 微信登录Service
 * @author system
 */
@Slf4j
@Service
public class WxLoginService {

    @Resource
    private UserMapper userMapper;

    // TODO: 配置微信小程序AppID和AppSecret
    private static final String WX_APPID = "your_wx_appid";
    private static final String WX_SECRET = "your_wx_secret";

    /**
     * 微信小程序登录
     * 注意：这是简化版本，实际使用需要调用微信API获取openid
     */
    @Transactional(rollbackFor = Exception.class)
    public UserLoginResponseDTO wxLogin(WxLoginDTO loginDTO) {
        try {
            // TODO: 调用微信API，使用code换取openid和session_key
            // String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + WX_APPID 
            //     + "&secret=" + WX_SECRET + "&js_code=" + loginDTO.getCode() + "&grant_type=authorization_code";
            // 这里需要发送HTTP请求到微信服务器
            
            // 临时方案：使用code作为唯一标识(实际应该使用openid)
            String openid = "wx_" + loginDTO.getCode();
            
            // 查询用户是否已存在
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, openid);
            User user = userMapper.selectOne(queryWrapper);

            if (user == null) {
                // 新用户，自动注册
                user = User.builder()
                        .userType("USER")
                        .username(openid)
                        .nickname(loginDTO.getNickname() != null ? loginDTO.getNickname() : "微信用户")
                        .avatar(loginDTO.getAvatar())
                        .phone(loginDTO.getPhone())
                        .memberType(0)
                        .status(1)
                        .build();
                
                userMapper.insert(user);
                log.info("微信新用户注册成功: {}", openid);
            } else {
                // 老用户，更新信息
                boolean needUpdate = false;
                if (loginDTO.getNickname() != null && !loginDTO.getNickname().equals(user.getNickname())) {
                    user.setNickname(loginDTO.getNickname());
                    needUpdate = true;
                }
                if (loginDTO.getAvatar() != null && !loginDTO.getAvatar().equals(user.getAvatar())) {
                    user.setAvatar(loginDTO.getAvatar());
                    needUpdate = true;
                }
                if (needUpdate) {
                    userMapper.updateById(user);
                }
                log.info("微信用户登录: {}", openid);
            }

            // 生成JWT token
            String token = JwtTokenUtils.generateToken(user.getId(), user.getUsername(), user.getUserType());

            // 构建响应
            UserDetailResponseDTO userInfo = UserConvert.entityToDetailResponse(user);
            return UserConvert.buildLoginResponse(token, userInfo);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("微信登录失败", e);
            throw new ServiceException("登录失败，请稍后重试");
        }
    }

    /**
     * 绑定手机号
     * 这个方法用于在登录后绑定手机号
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindPhone(Long userId, String phone) {
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }

            // 检查手机号是否已被其他用户绑定
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone)
                    .ne(User::getId, userId);
            if (userMapper.selectCount(queryWrapper) > 0) {
                throw new BusinessException("该手机号已被其他用户绑定");
            }

            user.setPhone(phone);
            userMapper.updateById(user);
            log.info("用户绑定手机号成功，用户ID: {}", userId);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("绑定手机号失败", e);
            throw new ServiceException("绑定失败");
        }
    }
}
