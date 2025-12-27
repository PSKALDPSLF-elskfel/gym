import request from '@/utils/request'

// ========== 简单文件上传接口（不保存在数据库） ==========

/**
 * 简单图片上传
 * 功能描述：上传图片文件，返回访问路径
 * 入参：file - 图片文件
 * 返回参数：string - 图片访问路径
 * url地址：/file/simple/upload/image
 * 请求方式：POST
 */
export function uploadImage(file, callbacks) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request.post('/file/simple/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    ...callbacks
  })
}

/**
 * 简单文件上传
 * 功能描述：上传通用类型文件，返回访问路径
 * 入参：{ file: File, type: string }
 * 返回参数：string - 文件访问路径
 * url地址：/file/simple/upload
 * 请求方式：POST
 */
export function uploadSimpleFile(file, fileType = 'COMMON', callbacks) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('type', fileType)
  
  return request.post('/file/simple/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    ...callbacks
  })
}

// ========== 完整业务文件管理接口 ==========

/**
 * 业务文件上传
 * 功能描述：上传文件并绑定业务对象，可选择是否替换旧文件
 * 入参：{ file: File, businessType: string, businessId: string, businessField: string, replaceOld: boolean }
 * 返回参数：FileInfoDTO - { id, fileName, filePath, fileUrl, businessType, businessId, businessField, ... }
 * url地址：/file/upload
 * 请求方式：POST
 */
export function uploadBusinessFile(file, businessType, businessId, businessField, replaceOld = false, callbacks) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('businessType', businessType)
  formData.append('businessId', businessId)
  if (businessField) {
    formData.append('businessField', businessField)
  }
  formData.append('replaceOld', replaceOld)
  
  return request.post('/file/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    ...callbacks
  })
}

/**
 * 临时文件上传
 * 功能描述：上传临时文件，后续可确认转为正式文件
 * 入参：file - 文件对象
 * 返回参数：FileInfoDTO
 * url地址：/file/upload/temp
 * 请求方式：POST
 */
export function uploadTempFile(file, callbacks) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request.post('/file/upload/temp', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    ...callbacks
  })
}

/**
 * 临时业务文件上传
 * 功能描述：上传临时业务文件，业务ID为0，标记为临时文件
 * 入参：{ file: File, businessType: string, businessField: string }
 * 返回参数：FileInfoDTO
 * url地址：/file/upload/temp-business
 * 请求方式：POST
 */
export function uploadTempBusinessFile(file, businessType, businessField, callbacks) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('businessType', businessType)
  if (businessField) {
    formData.append('businessField', businessField)
  }
  
  return request.post('/file/upload/temp-business', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    ...callbacks
  })
}

/**
 * 确认临时文件
 * 功能描述：将临时文件确认为正式文件并绑定业务对象
 * 入参：{ tempFileId: number, uploadDTO: { businessType, businessId, businessField } }
 * 返回参数：FileInfoDTO
 * url地址：/file/confirm/{tempFileId}
 * 请求方式：PUT
 */
export function confirmTempFile(tempFileId, uploadDTO, callbacks) {
  return request.put(`/file/confirm/${tempFileId}`, uploadDTO, callbacks)
}

/**
 * 获取业务文件列表
 * 功能描述：根据业务类型和业务ID获取文件列表
 * 入参：{ businessType: string, businessId: string }
 * 返回参数：FileInfoDTO[]
 * url地址：/file/business/{businessType}/{businessId}
 * 请求方式：GET
 */
export function getFilesByBusiness(businessType, businessId, callbacks) {
  return request.get(`/file/business/${businessType}/${businessId}`, null, callbacks)
}

/**
 * 获取业务字段文件
 * 功能描述：根据业务类型、业务ID和字段名获取文件
 * 入参：{ businessType: string, businessId: number, businessField: string }
 * 返回参数：FileInfoDTO[]
 * url地址：/file/business/{businessType}/{businessId}/{businessField}
 * 请求方式：GET
 */
export function getFilesByBusinessField(businessType, businessId, businessField, callbacks) {
  return request.get(`/file/business/${businessType}/${businessId}/${businessField}`, null, callbacks)
}

/**
 * 删除业务文件
 * 功能描述：删除指定业务文件
 * 入参：fileId - 文件ID
 * 返回参数：boolean
 * url地址：/file/{fileId}
 * 请求方式：DELETE
 */
export function deleteFile(fileId, callbacks) {
  return request.delete(`/file/${fileId}`, callbacks)
}

/**
 * 批量删除业务文件
 * 功能描述：根据业务信息批量删除文件
 * 入参：{ businessType: string, businessId: number, businessField?: string }
 * 返回参数：boolean
 * url地址：/file/business/{businessType}/{businessId}
 * 请求方式：DELETE
 */
export function deleteFilesByBusiness(businessType, businessId, businessField, callbacks) {
  const url = businessField 
    ? `/file/business/${businessType}/${businessId}?businessField=${businessField}`
    : `/file/business/${businessType}/${businessId}`
  return request.delete(url, callbacks)
}

/**
 * 获取文件上传配置
 * 功能描述：获取指定业务类型的文件上传配置信息
 * 入参：businessType - 业务类型
 * 返回参数：BussinessFileUploadConfig
 * url地址：/file/upload/config
 * 请求方式：GET
 */
export function getUploadConfig(businessType, callbacks) {
  return request.get('/file/upload/config', { businessType }, callbacks)
}
