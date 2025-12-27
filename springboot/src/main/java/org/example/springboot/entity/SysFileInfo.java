package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.IdType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import cn.hutool.core.io.FileUtil;

import java.time.LocalDateTime;

/**
 * 文件信息实体类
 * @author system
 */
@Data
@TableName("sys_file_info")
@Schema(description = "文件信息实体类")
public class SysFileInfo {

    @TableId(type = IdType.AUTO)
    @Schema(description = "文件ID")
    private Long id;

    @TableField("original_name")
    @Schema(description = "原始文件名")
    private String originalName;

    @TableField("file_path")
    @Schema(description = "文件访问路径")
    private String filePath;

    @TableField("file_size")
    @Schema(description = "文件大小(字节)")
    private Long fileSize;

    @TableField("file_type")
    @Schema(description = "文件类型(IMG/PDF/TXT等)")
    private String fileType;

    @TableField("business_type")
    @Schema(description = "业务类型")
    private String businessType;

    @TableField("business_id")
    @Schema(description = "业务对象ID")
    private String businessId;

    @TableField("business_field")
    @Schema(description = "业务字段名")
    private String businessField;

    @TableField("is_temp")
    @Schema(description = "是否临时文件(0:否 1:是)")
    private Integer isTemp;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 获取文件名（从文件路径中提取）
     */
    public String getFileName() {
        if (this.filePath == null) {
            return null;
        }
        return this.filePath.substring(this.filePath.lastIndexOf('/') + 1);
    }

    /**
     * 获取文件扩展名（从原始文件名中提取）
     */
    public String getFileExtension() {
        if (this.originalName == null) {
            return null;
        }
        return FileUtil.extName(this.originalName);
    }

    /**
     * 是否为临时文件
     */
    public boolean isTempFile() {
        return this.isTemp != null && this.isTemp == 1;
    }
} 