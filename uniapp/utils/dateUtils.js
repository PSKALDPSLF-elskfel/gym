/**
 * 日期格式化工具类
 * Uniapp版本 - 与Vue3项目完全一致
 */
export default class DateUtils {
  /**
   * 将日期字符串转换为iOS兼容格式
   * iOS不支持 "YYYY-MM-DD HH:mm:ss" 格式，需要转换为 "YYYY/MM/DD HH:mm:ss"
   * @param {string} dateString 日期字符串
   * @returns {string} iOS兼容的日期字符串
   */
  static toIOSCompatibleFormat(dateString) {
    if (typeof dateString === 'string') {
      // 将 "YYYY-MM-DD HH:mm:ss" 或 "YYYY-MM-DD" 格式转换为 "YYYY/MM/DD HH:mm:ss" 或 "YYYY/MM/DD"
      return dateString.replace(/-/g, '/');
    }
    return dateString;
  }

  /**
   * 格式化日期
   * @param {Date|string|number} date 日期对象/日期字符串/时间戳
   * @param {string} format 格式化模式 支持：
   * YYYY: 年
   * MM: 月
   * DD: 日
   * HH: 时
   * mm: 分
   * ss: 秒
   * @returns {string} 格式化后的日期字符串
   */
  static format(date, format = 'YYYY-MM-DD') {
    // 转换输入日期为Date对象
    let dateObj;
    if (date instanceof Date) {
      dateObj = date;
    } else {
      // 处理字符串格式，确保iOS兼容
      const compatibleDate = this.toIOSCompatibleFormat(date);
      dateObj = new Date(compatibleDate);
    }
    
    // 如果日期无效则返回空字符串
    if (isNaN(dateObj.getTime())) {
      return '';
    }

    // 获取日期各个部分
    const year = dateObj.getFullYear();
    const month = dateObj.getMonth() + 1;
    const day = dateObj.getDate();
    const hours = dateObj.getHours();
    const minutes = dateObj.getMinutes();
    const seconds = dateObj.getSeconds();

    // 补零函数
    const padZero = (num) => num.toString().padStart(2, '0');

    // 替换格式字符串
    return format
      .replace('YYYY', year)
      .replace('MM', padZero(month))
      .replace('DD', padZero(day))
      .replace('HH', padZero(hours))
      .replace('mm', padZero(minutes))
      .replace('ss', padZero(seconds));
  }

  /**
   * 格式化为年月 YYYY-MM
   * @param {Date|string|number} date 日期
   * @returns {string}
   */
  static formatYearMonth(date) {
    return this.format(date, 'YYYY-MM');
  }

  /**
   * 格式化为年月日 YYYY-MM-DD
   * @param {Date|string|number} date 日期
   * @returns {string}
   */
  static formatDate(date) {
    return this.format(date, 'YYYY-MM-DD');
  }

  /**
   * 格式化为完整日期时间 YYYY-MM-DD HH:mm:ss
   * @param {Date|string|number} date 日期
   * @returns {string}
   */
  static formatDateTime(date) {
    return this.format(date, 'YYYY-MM-DD HH:mm:ss');
  }
}

// 导出常用函数的快捷方式
// formatDate 支持两种用法：
// 1. formatDate(date) - 格式化为 YYYY-MM-DD
// 2. formatDate(date, format) - 自定义格式
export const formatDate = (date, formatStr) => {
  if (formatStr) {
    return DateUtils.format(date, formatStr)
  }
  return DateUtils.formatDate(date)
}
export const formatDateTime = (date) => DateUtils.formatDateTime(date)
export const formatYearMonth = (date) => DateUtils.formatYearMonth(date)
export const format = (date, formatStr) => DateUtils.format(date, formatStr)

