package org.example.springboot.service.convert;

import org.example.springboot.dto.response.UserMembershipResponseDTO;
import org.example.springboot.entity.MembershipPackage;
import org.example.springboot.entity.User;
import org.example.springboot.entity.UserMembership;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 用户会员记录转换类
 * @author system
 */
public class UserMembershipConvert {

    /**
     * Entity转ResponseDTO
     */
    public static UserMembershipResponseDTO toResponseDTO(UserMembership entity) {
        if (entity == null) {
            return null;
        }

        UserMembershipResponseDTO dto = new UserMembershipResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setPackageId(entity.getPackageId());
        dto.setMemberType(entity.getMemberType());
        dto.setMemberTypeName(entity.getMemberTypeDisplayName());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setPrice(entity.getPrice());
        dto.setStatus(entity.getStatus());
        dto.setStatusName(entity.getStatusDisplayName());
        dto.setPurchaseTime(entity.getPurchaseTime());

        // 计算剩余天数
        if (entity.getEndTime() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(entity.getEndTime())) {
                dto.setRemainingDays(ChronoUnit.DAYS.between(now, entity.getEndTime()));
                dto.setExpired(false);
            } else {
                dto.setRemainingDays(0L);
                dto.setExpired(true);
            }
        } else {
            dto.setRemainingDays(0L);
            dto.setExpired(true);
        }

        return dto;
    }

    /**
     * Entity转ResponseDTO（包含用户和套餐信息）
     */
    public static UserMembershipResponseDTO toResponseDTO(UserMembership entity, User user, MembershipPackage membershipPackage) {
        UserMembershipResponseDTO dto = toResponseDTO(entity);
        if (dto == null) {
            return null;
        }

        if (user != null) {
            dto.setUserNickname(user.getNickname());
        }

        if (membershipPackage != null) {
            dto.setPackageName(membershipPackage.getName());
        }

        return dto;
    }
}
