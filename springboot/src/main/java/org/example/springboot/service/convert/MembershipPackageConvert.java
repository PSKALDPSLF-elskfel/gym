package org.example.springboot.service.convert;

import org.example.springboot.dto.command.MembershipPackageCreateDTO;
import org.example.springboot.dto.command.MembershipPackageUpdateDTO;
import org.example.springboot.dto.response.MembershipPackageResponseDTO;
import org.example.springboot.entity.MembershipPackage;

import java.time.LocalDateTime;

/**
 * 会员套餐转换类
 * @author system
 */
public class MembershipPackageConvert {

    /**
     * 创建命令DTO转换为实体
     * @param createDTO 创建命令DTO
     * @return MembershipPackage实体
     */
    public static MembershipPackage createCommandToEntity(MembershipPackageCreateDTO createDTO) {
        return MembershipPackage.builder()
                .name(createDTO.getName())
                .memberType(createDTO.getMemberType())
                .durationDays(createDTO.getDurationDays())
                .price(createDTO.getPrice())
                .description(createDTO.getDescription())
                .benefits(createDTO.getBenefits())
                .status(createDTO.getStatus())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * 更新命令DTO转换为实体
     * @param updateDTO 更新命令DTO
     * @return MembershipPackage实体
     */
    public static MembershipPackage updateCommandToEntity(MembershipPackageUpdateDTO updateDTO) {
        return MembershipPackage.builder()
                .id(updateDTO.getId())
                .name(updateDTO.getName())
                .memberType(updateDTO.getMemberType())
                .durationDays(updateDTO.getDurationDays())
                .price(updateDTO.getPrice())
                .description(updateDTO.getDescription())
                .benefits(updateDTO.getBenefits())
                .status(updateDTO.getStatus())
                .updateTime(LocalDateTime.now())
                .build();
    }

    /**
     * 实体转换为响应DTO
     * @param entity MembershipPackage实体
     * @return 会员套餐响应DTO
     */
    public static MembershipPackageResponseDTO entityToResponse(MembershipPackage entity) {
        return MembershipPackageResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .memberType(entity.getMemberType())
                .memberTypeDisplayName(entity.getMemberTypeDisplayName())
                .durationDays(entity.getDurationDays())
                .price(entity.getPrice())
                .description(entity.getDescription())
                .benefits(entity.getBenefits())
                .status(entity.getStatus())
                .statusDisplayName(entity.getStatusDisplayName())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}
