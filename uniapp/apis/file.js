import { getBaseUrl } from '@/config/site.js'
import { getToken } from '@/utils/auth.js'

/**
 * 上传图片文件
 * 功能描述：上传单个图片文件到服务器
 * 入参：filePath - 本地文件路径
 * 返回参数：文件访问路径 string
 * url地址：/file/simple/upload/image
 * 请求方式：POST
 */
export function uploadImage(filePath) {
  return new Promise((resolve, reject) => {
    const token = getToken()
    const baseUrl = getBaseUrl()
    
    uni.uploadFile({
      url: `${baseUrl}/file/simple/upload/image`,
      filePath: filePath,
      name: 'file',
      header: {
        'Authorization': token ? `Bearer ${token}` : ''
      },
      success: (res) => {
        if (res.statusCode === 200) {
          try {
            const data = JSON.parse(res.data)
            if (data.code === "200" || data.code === 200) {
              resolve(data.data)
            } else {
              reject(new Error(data.msg || '上传失败'))
            }
          } catch (e) {
            reject(new Error('解析响应数据失败'))
          }
        } else {
          reject(new Error(`上传失败，状态码：${res.statusCode}`))
        }
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

/**
 * 上传普通文件
 * 功能描述：上传通用类型文件到服务器
 * 入参：{ filePath: string, fileType: string }
 * 返回参数：文件访问路径 string
 * url地址：/file/simple/upload
 * 请求方式：POST
 */
export function uploadFile(filePath, fileType = 'COMMON') {
  return new Promise((resolve, reject) => {
    const token = getToken()
    const baseUrl = getBaseUrl()
    
    uni.uploadFile({
      url: `${baseUrl}/file/simple/upload`,
      filePath: filePath,
      name: 'file',
      header: {
        'Authorization': token ? `Bearer ${token}` : ''
      },
      formData: {
        type: fileType
      },
      success: (res) => {
        if (res.statusCode === 200) {
          try {
            const data = JSON.parse(res.data)
            if (data.code === "200" || data.code === 200) {
              resolve(data.data)
            } else {
              reject(new Error(data.msg || '上传失败'))
            }
          } catch (e) {
            reject(new Error('解析响应数据失败'))
          }
        } else {
          reject(new Error(`上传失败，状态码：${res.statusCode}`))
        }
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

/**
 * 上传业务文件
 * 功能描述：上传文件并绑定业务对象
 * 入参：{ filePath, businessType, businessId, businessField, replaceOld }
 * 返回参数：FileInfoDTO
 * url地址：/file/upload
 * 请求方式：POST
 */
export function uploadBusinessFile(filePath, businessType, businessId, businessField = '', replaceOld = false) {
  return new Promise((resolve, reject) => {
    const token = getToken()
    const baseUrl = getBaseUrl()
    
    const formData = {
      businessType: businessType,
      businessId: businessId,
      replaceOld: replaceOld ? 'true' : 'false'
    }
    
    if (businessField) {
      formData.businessField = businessField
    }
    
    uni.uploadFile({
      url: `${baseUrl}/file/upload`,
      filePath: filePath,
      name: 'file',
      header: {
        'Authorization': token ? `Bearer ${token}` : ''
      },
      formData: formData,
      success: (res) => {
        if (res.statusCode === 200) {
          try {
            const data = JSON.parse(res.data)
            if (data.code === "200" || data.code === 200) {
              resolve(data.data)
            } else {
              reject(new Error(data.msg || '上传失败'))
            }
          } catch (e) {
            reject(new Error('解析响应数据失败'))
          }
        } else {
          reject(new Error(`上传失败，状态码：${res.statusCode}`))
        }
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

/**
 * 批量上传图片
 * 功能描述：上传多个图片文件
 * 入参：filePaths - 本地文件路径数组
 * 返回参数：文件访问路径数组
 */
export async function uploadImages(filePaths) {
  const results = []
  for (const filePath of filePaths) {
    try {
      const url = await uploadImage(filePath)
      results.push(url)
    } catch (error) {
      throw error
    }
  }
  return results
}
