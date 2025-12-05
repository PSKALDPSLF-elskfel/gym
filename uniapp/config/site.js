/**
 * 站点配置文件
 * Uniapp版本 - 与Vue3项目保持一致
 */

export const siteConfig = {
  // 站点基本信息
  name: '心理健康打卡社区',
  shortName: '心理健康',
  description: '关爱心理健康，从每一天开始',
  slogan: '让心理健康成为一种习惯',
  
  // Logo配置
  logo: {
    icon: '/static/logo.png',
    text: '心理健康打卡社区'
  },
  
  // 版权信息
  copyright: {
    year: '2025',
    owner: '心理健康打卡社区',
    icp: '',
    text: '© 2025 心理健康打卡社区. All rights reserved.'
  },
  
  // 联系方式
  contact: {
    email: 'support@mental-health.com',
    phone: '400-123-4567',
    address: '北京市朝阳区xxx路xxx号'
  },
  
  // SEO配置
  seo: {
    keywords: '心理健康,打卡,社区,心理咨询',
    author: '心理健康团队'
  }
}

/**
 * 获取API基础地址
 * 根据平台和环境返回不同的地址
 */
export function getBaseUrl() {
  const isDev = true // 手动切换环境：true=开发环境, false=生产环境
  
  // #ifdef H5
  // H5端使用代理
  if (isDev) {
    return '/api' // 通过 manifest.json 或 vite.config.js 配置代理
  }
  return 'https://api.yourdomain.com/api'
  // #endif
  
  // #ifndef H5
  // 小程序/App端使用完整地址
  if (isDev) {
    return 'http://localhost:8888/api'
  }
  return 'https://api.yourdomain.com/api'
  // #endif
}

/**
 * 获取完整的版权信息
 */
export function getCopyright() {
  return `© ${siteConfig.copyright.year} ${siteConfig.copyright.owner}. All rights reserved.`
}

/**
 * 获取站点标题
 */
export function getSiteTitle(pageTitle = '') {
  return pageTitle ? `${pageTitle} - ${siteConfig.name}` : siteConfig.name
}

export default siteConfig

