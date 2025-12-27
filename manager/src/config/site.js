/**
 * 站点配置文件
 * 统一管理站点信息、描述、版权等
 */

export const siteConfig = {
  // 站点基本信息
  name: '健身房管理系统',
  shortName: '健身房',
  description: '健身房管理系统',
  slogan: '让健身成为一种习惯',
  
  // Logo配置
  logo: {
    icon: '/src/assets/logo.png', // Logo图片路径
    text: '健身房管理系统'
  },
  
  // 后台管理系统配置
  admin: {
    name: '健身房管理系统',
    shortName: '管理后台',
    logo: {
      icon: '/src/assets/logo.png', // Logo图片路径
      text: '健身房管理系统'
    }
  },
  
  // 版权信息
  copyright: {
    year: '2025',
    owner: '健身房管理系统',
    icp: '', // ICP备案号
    text: '© 2025 健身房管理系统 '
  },
  



  

}

/**
 * 获取完整的版权信息
 */
export function getCopyright() {
  return `© ${siteConfig.copyright.year} ${siteConfig.copyright.owner}. All rights reserved.`
}

/**
 * 获取站点标题（用于页面title）
 */
export function getSiteTitle(pageTitle = '') {
  return pageTitle ? `${pageTitle} - ${siteConfig.name}` : siteConfig.name
}

/**
 * 获取后台标题
 */
export function getAdminTitle(pageTitle = '') {
  return pageTitle ? `${pageTitle} - ${siteConfig.admin.name}` : siteConfig.admin.name
}

export default siteConfig
