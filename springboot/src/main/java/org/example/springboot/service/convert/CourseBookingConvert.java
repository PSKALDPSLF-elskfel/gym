package org.example.springboot.service.convert;

import org.example.springboot.dto.response.CourseBookingDetailDTO;
import org.example.springboot.dto.response.CourseBookingResponseDTO;
import org.example.springboot.entity.GymCourse;
import org.example.springboot.entity.GymCourseBooking;
import org.example.springboot.entity.GymCourseSchedule;
import org.example.springboot.entity.User;

/**
 * 课程预约转换类
 * @author system
 */
public class CourseBookingConvert {

    /**
     * Entity转ResponseDTO
     */
    public static CourseBookingResponseDTO toResponseDTO(GymCourseBooking entity) {
        if (entity == null) {
            return null;
        }

        CourseBookingResponseDTO dto = new CourseBookingResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setScheduleId(entity.getScheduleId());
        dto.setBookingTime(entity.getBookingTime());
        dto.setOriginalPrice(entity.getOriginalPrice());
        dto.setActualPrice(entity.getActualPrice());
        dto.setDiscountRate(entity.getDiscountRate());
        dto.setStatus(entity.getStatus());
        dto.setStatusDisplayName(entity.getStatusDisplayName());
        dto.setCancelTime(entity.getCancelTime());
        dto.setCreateTime(entity.getCreateTime());
        dto.setSavedAmount(entity.getSavedAmount());

        return dto;
    }

    /**
     * Entity转DetailDTO(包含课程和用户信息)
     */
    public static CourseBookingDetailDTO toDetailDTO(GymCourseBooking booking, 
                                                      User user, 
                                                      GymCourseSchedule schedule, 
                                                      GymCourse course) {
        if (booking == null) {
            return null;
        }

        CourseBookingDetailDTO dto = new CourseBookingDetailDTO();
        
        // 预约信息
        dto.setId(booking.getId());
        dto.setUserId(booking.getUserId());
        dto.setScheduleId(booking.getScheduleId());
        dto.setBookingTime(booking.getBookingTime());
        dto.setOriginalPrice(booking.getOriginalPrice());
        dto.setActualPrice(booking.getActualPrice());
        dto.setDiscountRate(booking.getDiscountRate());
        dto.setStatus(booking.getStatus());
        dto.setStatusDisplayName(booking.getStatusDisplayName());
        dto.setCancelTime(booking.getCancelTime());
        dto.setCreateTime(booking.getCreateTime());
        dto.setSavedAmount(booking.getSavedAmount());

        // 用户信息
        if (user != null) {
            dto.setUserNickname(user.getNickname());
            dto.setUserAvatar(user.getAvatar());
            dto.setUserPhone(user.getPhone());
            dto.setUserMemberType(user.getMemberType());
            dto.setUserMemberTypeDisplayName(user.getMemberTypeDisplayName());
        }

        // 课程时间信息
        if (schedule != null) {
            dto.setScheduleStartTime(schedule.getStartTime());
            dto.setScheduleEndTime(schedule.getEndTime());
            dto.setCourseId(schedule.getCourseId());
        }

        // 课程信息
        if (course != null) {
            dto.setCourseName(course.getName());
            dto.setCourseCoverImage(course.getCoverImage());
            dto.setCourseDuration(course.getDuration());
        }

        return dto;
    }
}
