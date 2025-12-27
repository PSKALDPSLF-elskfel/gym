import request from '@/utils/request'

/**
 * 上传图片文件
 * @param {File} file - 文件对象
 * @returns {Promise}
 */
export const uploadImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  
  return request.post('/files/simple/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传通用文件
 * @param {File} file - 文件对象
 * @param {String} fileType - 文件类型
 * @returns {Promise}
 */
export const uploadFile = (file, fileType = 'COMMON') => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('type', fileType)
  
  return request.post('/files/simple/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 删除文件
 * @param {String} filename - 文件名
 * @returns {Promise}
 */
export const deleteFile = (filename) => {
  return request.delete(`/files/simple/delete/${filename}`)
}
