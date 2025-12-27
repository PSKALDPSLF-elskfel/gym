package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.entity.GymAnnouncement;
import org.example.springboot.exception.BusinessException;
import org.example.springboot.mapper.GymAnnouncementMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 公告服务类
 * @author system
 */
@Slf4j
@Service
public class AnnouncementService {

    @Resource
    private GymAnnouncementMapper announcementMapper;

    /**
     * 获取最新的启用公告列表
     * @param limit 限制数量
     * @return 公告列表
     */
    public List<GymAnnouncement> getLatestAnnouncements(Integer limit) {
        log.info("获取最新公告，限制数量: {}", limit);
        
        LambdaQueryWrapper<GymAnnouncement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymAnnouncement::getStatus, 1); // 只查询启用的公告
        wrapper.orderByDesc(GymAnnouncement::getCreateTime); // 按创建时间倒序
        wrapper.last("LIMIT " + (limit != null ? limit : 5)); // 默认最多5条
        
        return announcementMapper.selectList(wrapper);
    }

    /**
     * 获取所有启用的公告
     * @return 公告列表
     */
    public List<GymAnnouncement> getAllEnabledAnnouncements() {
        LambdaQueryWrapper<GymAnnouncement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymAnnouncement::getStatus, 1);
        wrapper.orderByDesc(GymAnnouncement::getCreateTime);
        
        return announcementMapper.selectList(wrapper);
    }

    /**
     * 分页查询公告(管理端)
     */
    public Page<GymAnnouncement> selectPage(Long current, Long size, String title, Integer status) {
        log.info("分页查询公告: current={}, size={}, title={}, status={}", current, size, title, status);
        
        Page<GymAnnouncement> page = new Page<>(current, size);
        LambdaQueryWrapper<GymAnnouncement> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(title)) {
            wrapper.like(GymAnnouncement::getTitle, title);
        }
        if (status != null) {
            wrapper.eq(GymAnnouncement::getStatus, status);
        }
        
        wrapper.orderByDesc(GymAnnouncement::getCreateTime);
        
        return announcementMapper.selectPage(page, wrapper);
    }

    /**
     * 创建公告
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(GymAnnouncement announcement) {
        log.info("创建公告: {}", announcement.getTitle());
        
        if (!StringUtils.hasText(announcement.getTitle())) {
            throw new BusinessException("公告标题不能为空");
        }
        if (!StringUtils.hasText(announcement.getContent())) {
            throw new BusinessException("公告内容不能为空");
        }
        
        int result = announcementMapper.insert(announcement);
        if (result <= 0) {
            throw new BusinessException("创建公告失败");
        }
    }

    /**
     * 更新公告
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(GymAnnouncement announcement) {
        log.info("更新公告: id={}", announcement.getId());
        
        GymAnnouncement existing = announcementMapper.selectById(announcement.getId());
        if (existing == null) {
            throw new BusinessException("公告不存在");
        }
        
        if (!StringUtils.hasText(announcement.getTitle())) {
            throw new BusinessException("公告标题不能为空");
        }
        if (!StringUtils.hasText(announcement.getContent())) {
            throw new BusinessException("公告内容不能为空");
        }
        
        int result = announcementMapper.updateById(announcement);
        if (result <= 0) {
            throw new BusinessException("更新公告失败");
        }
    }

    /**
     * 删除公告
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("删除公告: id={}", id);
        
        GymAnnouncement existing = announcementMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("公告不存在");
        }
        
        int result = announcementMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除公告失败");
        }
    }

    /**
     * 更新公告状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        log.info("更新公告状态: id={}, status={}", id, status);
        
        GymAnnouncement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        
        announcement.setStatus(status);
        int result = announcementMapper.updateById(announcement);
        if (result <= 0) {
            throw new BusinessException("更新状态失败");
        }
    }
}
