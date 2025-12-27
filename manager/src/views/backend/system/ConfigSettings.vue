<template>
  <div class="config-settings">
    <a-card title="系统配置" :bordered="false">
      <a-tabs v-model:activeKey="activeTab" type="card">
        <!-- 微信配置 -->
        <a-tab-pane key="wechat" tab="微信配置">
          <a-spin :spinning="loading">
            <a-form
              ref="wechatFormRef"
              :model="wechatConfig"
              :label-col="{ span: 6 }"
              :wrapper-col="{ span: 14 }"
              class="config-form"
            >
              <a-divider orientation="left">
                <i class="fab fa-weixin" style="color: #1890ff; margin-right: 8px;"></i>
                小程序配置
              </a-divider>
              
              <a-form-item label="小程序AppID" name="appId">
                <a-input
                  v-model:value="wechatConfig.appId"
                  placeholder="请输入小程序AppID"
                  allow-clear
                >
                  <template #prefix>
                    <i class="fab fa-weixin" style="color: #1890ff;"></i>
                  </template>
                </a-input>
                <div class="form-tip">示例: wx1234567890abcdef</div>
              </a-form-item>

              <a-form-item label="小程序AppSecret" name="appSecret">
                <a-input-password
                  v-model:value="wechatConfig.appSecret"
                  placeholder="请输入小程序AppSecret"
                  allow-clear
                />
                <div class="form-tip">用于获取小程序access_token</div>
              </a-form-item>

              <a-divider orientation="left">
                <i class="fas fa-credit-card" style="color: #52c41a; margin-right: 8px;"></i>
                微信支付配置
              </a-divider>

              <a-form-item label="商户号" name="mchId">
                <a-input
                  v-model:value="wechatConfig.mchId"
                  placeholder="请输入商户号"
                  allow-clear
                >
                  <template #prefix>
                    <i class="fas fa-store" style="color: #52c41a;"></i>
                  </template>
                </a-input>
                <div class="form-tip">示例: 1234567890</div>
              </a-form-item>

              <a-form-item label="API V3密钥" name="apiV3Key">
                <a-input-password
                  v-model:value="wechatConfig.apiV3Key"
                  placeholder="请输入API V3密钥"
                  allow-clear
                />
                <div class="form-tip">32位字符，用于API V3接口签名验证</div>
              </a-form-item>

              <a-form-item label="支付回调地址" name="notifyUrl">
                <a-input
                  v-model:value="wechatConfig.notifyUrl"
                  placeholder="请输入支付回调地址"
                  allow-clear
                >
                  <template #prefix>
                    <i class="fas fa-link"></i>
                  </template>
                </a-input>
                <div class="form-tip">示例: https://yourdomain.com/api/pay/notify</div>
              </a-form-item>

              <a-form-item label="商户私钥路径" name="privateKeyPath">
                <a-input
                  v-model:value="wechatConfig.privateKeyPath"
                  placeholder="请输入商户私钥路径"
                  allow-clear
                />
                <div class="form-tip">示例: classpath:apiclient_key.pem</div>
              </a-form-item>

              <a-form-item :wrapper-col="{ offset: 6, span: 14 }">
                <a-space>
                  <a-button type="primary" @click="saveWechatConfig" :loading="saving">
                    <template #icon><SaveOutlined /></template>
                    保存配置
                  </a-button>
                  <a-button @click="loadWechatConfig">
                    <template #icon><ReloadOutlined /></template>
                    重置
                  </a-button>
                </a-space>
              </a-form-item>
            </a-form>
          </a-spin>
        </a-tab-pane>

        <!-- 文件上传配置 -->
        <a-tab-pane key="file" tab="文件上传配置">
          <a-spin :spinning="loading">
            <a-form
              ref="fileFormRef"
              :model="fileConfig"
              :label-col="{ span: 6 }"
              :wrapper-col="{ span: 14 }"
              class="config-form"
            >
              <a-divider orientation="left">
                <i class="fas fa-folder-open" style="color: #ff7a45; margin-right: 8px;"></i>
                上传路径配置
              </a-divider>

              <a-form-item label="文件存储路径" name="uploadPath">
                <a-input
                  v-model:value="fileConfig.uploadPath"
                  placeholder="请输入文件存储路径"
                  allow-clear
                >
                  <template #prefix>
                    <i class="fas fa-folder"></i>
                  </template>
                </a-input>
                <div class="form-tip">示例: ./files 或 /var/www/files</div>
              </a-form-item>

              <a-form-item label="访问路径前缀" name="accessPrefix">
                <a-input
                  v-model:value="fileConfig.accessPrefix"
                  placeholder="请输入访问路径前缀"
                  allow-clear
                >
                  <template #prefix>
                    <i class="fas fa-link"></i>
                  </template>
                </a-input>
                <div class="form-tip">示例: /files 或 https://cdn.yourdomain.com</div>
              </a-form-item>

              <a-divider orientation="left">
                <i class="fas fa-cog" style="color: #1890ff; margin-right: 8px;"></i>
                上传限制配置
              </a-divider>

              <a-form-item label="最大文件大小" name="maxSize">
                <a-input-number
                  v-model:value="fileConfig.maxSize"
                  :min="1"
                  :max="100"
                  style="width: 200px"
                />
                <span style="margin-left: 8px">MB</span>
                <div class="form-tip">单个文件最大上传大小（1-100MB）</div>
              </a-form-item>

              <a-form-item label="允许的文件类型" name="allowedTypes">
                <a-select
                  v-model:value="fileConfig.allowedTypes"
                  mode="tags"
                  placeholder="请选择或输入允许的文件类型"
                  style="width: 100%"
                  :options="fileTypeOptions"
                />
                <div class="form-tip">支持的文件扩展名，如: jpg, png, pdf</div>
              </a-form-item>

              <a-form-item label="图片压缩" name="imageCompress">
                <a-switch v-model:checked="fileConfig.imageCompress" />
                <span style="margin-left: 12px">
                  {{ fileConfig.imageCompress ? '已启用' : '已禁用' }}
                </span>
                <div class="form-tip">自动压缩上传的图片文件</div>
              </a-form-item>

              <a-form-item label="图片质量" name="imageQuality" v-if="fileConfig.imageCompress">
                <a-slider
                  v-model:value="fileConfig.imageQuality"
                  :min="10"
                  :max="100"
                  :marks="{ 10: '10%', 50: '50%', 80: '80%', 100: '100%' }"
                  style="width: 300px"
                />
                <div class="form-tip">压缩后的图片质量（10-100%）</div>
              </a-form-item>

              <a-form-item :wrapper-col="{ offset: 6, span: 14 }">
                <a-space>
                  <a-button type="primary" @click="saveFileConfig" :loading="saving">
                    <template #icon><SaveOutlined /></template>
                    保存配置
                  </a-button>
                  <a-button @click="loadFileConfig">
                    <template #icon><ReloadOutlined /></template>
                    重置
                  </a-button>
                </a-space>
              </a-form-item>
            </a-form>
          </a-spin>
        </a-tab-pane>

        <!-- 系统配置 -->
        <a-tab-pane key="system" tab="系统配置">
          <a-spin :spinning="loading">
            <a-form
              ref="systemFormRef"
              :model="systemConfig"
              :label-col="{ span: 6 }"
              :wrapper-col="{ span: 14 }"
              class="config-form"
            >
              <a-divider orientation="left">
                <i class="fas fa-info-circle" style="color: #1890ff; margin-right: 8px;"></i>
                基本信息
              </a-divider>

              <a-form-item label="系统名称" name="systemName">
                <a-input
                  v-model:value="systemConfig.systemName"
                  placeholder="请输入系统名称"
                  allow-clear
                />
              </a-form-item>

              <a-form-item label="系统版本" name="systemVersion">
                <a-input
                  v-model:value="systemConfig.systemVersion"
                  placeholder="请输入系统版本"
                  allow-clear
                />
              </a-form-item>

              <a-form-item label="系统描述" name="systemDescription">
                <a-textarea
                  v-model:value="systemConfig.systemDescription"
                  placeholder="请输入系统描述"
                  :rows="3"
                  allow-clear
                />
              </a-form-item>

              <a-divider orientation="left">
                <i class="fas fa-shield-alt" style="color: #52c41a; margin-right: 8px;"></i>
                安全配置
              </a-divider>

              <a-form-item label="会话超时时间" name="sessionTimeout">
                <a-input-number
                  v-model:value="systemConfig.sessionTimeout"
                  :min="10"
                  :max="1440"
                  style="width: 200px"
                />
                <span style="margin-left: 8px">分钟</span>
                <div class="form-tip">用户登录会话超时时间（10-1440分钟）</div>
              </a-form-item>

              <a-form-item label="密码复杂度检查" name="passwordCheck">
                <a-switch v-model:checked="systemConfig.passwordCheck" />
                <span style="margin-left: 12px">
                  {{ systemConfig.passwordCheck ? '已启用' : '已禁用' }}
                </span>
                <div class="form-tip">要求密码包含大小写字母、数字和特殊字符</div>
              </a-form-item>

              <a-form-item :wrapper-col="{ offset: 6, span: 14 }">
                <a-space>
                  <a-button type="primary" @click="saveSystemConfig" :loading="saving">
                    <template #icon><SaveOutlined /></template>
                    保存配置
                  </a-button>
                  <a-button @click="loadSystemConfig">
                    <template #icon><ReloadOutlined /></template>
                    重置
                  </a-button>
                </a-space>
              </a-form-item>
            </a-form>
          </a-spin>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { SaveOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import {
  getConfigValue,
  createSysConfig,
  updateSysConfig,
  getSysConfigPage
} from '@/api/SysConfigApi'

// 当前标签页
const activeTab = ref('wechat')
const loading = ref(false)
const saving = ref(false)

// 微信配置
const wechatFormRef = ref()
const wechatConfig = reactive({
  appId: '',
  appSecret: '',
  mchId: '',
  apiV3Key: '',
  notifyUrl: '',
  privateKeyPath: ''
})

// 文件上传配置
const fileFormRef = ref()
const fileConfig = reactive({
  uploadPath: './files',
  accessPrefix: '/files',
  maxSize: 10,
  allowedTypes: ['jpg', 'jpeg', 'png', 'gif', 'pdf', 'doc', 'docx', 'xls', 'xlsx'],
  imageCompress: true,
  imageQuality: 80
})

// 文件类型选项
const fileTypeOptions = [
  { label: 'JPG', value: 'jpg' },
  { label: 'JPEG', value: 'jpeg' },
  { label: 'PNG', value: 'png' },
  { label: 'GIF', value: 'gif' },
  { label: 'PDF', value: 'pdf' },
  { label: 'DOC', value: 'doc' },
  { label: 'DOCX', value: 'docx' },
  { label: 'XLS', value: 'xls' },
  { label: 'XLSX', value: 'xlsx' },
  { label: 'MP4', value: 'mp4' },
  { label: 'AVI', value: 'avi' }
]

// 系统配置
const systemFormRef = ref()
const systemConfig = reactive({
  systemName: '健身房预约管理系统',
  systemVersion: '1.0.0',
  systemDescription: '',
  sessionTimeout: 120,
  passwordCheck: true
})

// 配置键映射
const CONFIG_KEYS = {
  WECHAT: {
    APP_ID: 'wechat.appid',
    APP_SECRET: 'wechat.appsecret',
    MCH_ID: 'wechat.pay.mchid',
    API_V3_KEY: 'wechat.pay.apiv3key',
    NOTIFY_URL: 'wechat.pay.notifyurl',
    PRIVATE_KEY_PATH: 'wechat.pay.privatekeypath'
  },
  FILE: {
    UPLOAD_PATH: 'file.upload.path',
    ACCESS_PREFIX: 'file.access.prefix',
    MAX_SIZE: 'file.upload.maxsize',
    ALLOWED_TYPES: 'file.upload.allowedtypes',
    IMAGE_COMPRESS: 'file.image.compress',
    IMAGE_QUALITY: 'file.image.quality'
  },
  SYSTEM: {
    NAME: 'system.name',
    VERSION: 'system.version',
    DESCRIPTION: 'system.description',
    SESSION_TIMEOUT: 'system.session.timeout',
    PASSWORD_CHECK: 'system.password.check'
  }
}

// 加载微信配置
const loadWechatConfig = async () => {
  loading.value = true
  try {
    const keys = CONFIG_KEYS.WECHAT
    wechatConfig.appId = await fetchConfigValue(keys.APP_ID) || ''
    wechatConfig.appSecret = await fetchConfigValue(keys.APP_SECRET) || ''
    wechatConfig.mchId = await fetchConfigValue(keys.MCH_ID) || ''
    wechatConfig.apiV3Key = await fetchConfigValue(keys.API_V3_KEY) || ''
    wechatConfig.notifyUrl = await fetchConfigValue(keys.NOTIFY_URL) || ''
    wechatConfig.privateKeyPath = await fetchConfigValue(keys.PRIVATE_KEY_PATH) || 'classpath:apiclient_key.pem'
  } catch (error) {
    console.error('加载微信配置失败:', error)
  } finally {
    loading.value = false
  }
}

// 保存微信配置
const saveWechatConfig = async () => {
  saving.value = true
  try {
    const keys = CONFIG_KEYS.WECHAT
    await saveConfigValue(keys.APP_ID, wechatConfig.appId, '小程序AppID', '微信配置')
    await saveConfigValue(keys.APP_SECRET, wechatConfig.appSecret, '小程序AppSecret', '微信配置')
    await saveConfigValue(keys.MCH_ID, wechatConfig.mchId, '商户号', '微信配置')
    await saveConfigValue(keys.API_V3_KEY, wechatConfig.apiV3Key, 'API V3密钥', '微信配置')
    await saveConfigValue(keys.NOTIFY_URL, wechatConfig.notifyUrl, '支付回调地址', '微信配置')
    await saveConfigValue(keys.PRIVATE_KEY_PATH, wechatConfig.privateKeyPath, '商户私钥路径', '微信配置')
    
    message.success('微信配置保存成功')
  } catch (error) {
    message.error('保存失败：' + (error.message || '未知错误'))
  } finally {
    saving.value = false
  }
}

// 加载文件配置
const loadFileConfig = async () => {
  loading.value = true
  try {
    const keys = CONFIG_KEYS.FILE
    fileConfig.uploadPath = await fetchConfigValue(keys.UPLOAD_PATH) || './files'
    fileConfig.accessPrefix = await fetchConfigValue(keys.ACCESS_PREFIX) || '/files'
    fileConfig.maxSize = parseInt(await fetchConfigValue(keys.MAX_SIZE) || '10')
    
    const allowedTypesStr = await fetchConfigValue(keys.ALLOWED_TYPES)
    if (allowedTypesStr) {
      fileConfig.allowedTypes = allowedTypesStr.split(',')
    }
    
    const compressStr = await fetchConfigValue(keys.IMAGE_COMPRESS)
    fileConfig.imageCompress = compressStr === 'true'
    fileConfig.imageQuality = parseInt(await fetchConfigValue(keys.IMAGE_QUALITY) || '80')
  } catch (error) {
    console.error('加载文件配置失败:', error)
  } finally {
    loading.value = false
  }
}

// 保存文件配置
const saveFileConfig = async () => {
  saving.value = true
  try {
    const keys = CONFIG_KEYS.FILE
    await saveConfigValue(keys.UPLOAD_PATH, fileConfig.uploadPath, '文件存储路径', '文件配置')
    await saveConfigValue(keys.ACCESS_PREFIX, fileConfig.accessPrefix, '访问路径前缀', '文件配置')
    await saveConfigValue(keys.MAX_SIZE, fileConfig.maxSize.toString(), '最大文件大小(MB)', '文件配置')
    await saveConfigValue(keys.ALLOWED_TYPES, fileConfig.allowedTypes.join(','), '允许的文件类型', '文件配置')
    await saveConfigValue(keys.IMAGE_COMPRESS, fileConfig.imageCompress.toString(), '图片压缩', '文件配置')
    await saveConfigValue(keys.IMAGE_QUALITY, fileConfig.imageQuality.toString(), '图片质量', '文件配置')
    
    message.success('文件配置保存成功')
  } catch (error) {
    message.error('保存失败：' + (error.message || '未知错误'))
  } finally {
    saving.value = false
  }
}

// 加载系统配置
const loadSystemConfig = async () => {
  loading.value = true
  try {
    const keys = CONFIG_KEYS.SYSTEM
    systemConfig.systemName = await fetchConfigValue(keys.NAME) || '健身房预约管理系统'
    systemConfig.systemVersion = await fetchConfigValue(keys.VERSION) || '1.0.0'
    systemConfig.systemDescription = await fetchConfigValue(keys.DESCRIPTION) || ''
    systemConfig.sessionTimeout = parseInt(await fetchConfigValue(keys.SESSION_TIMEOUT) || '120')
    
    const passwordCheckStr = await fetchConfigValue(keys.PASSWORD_CHECK)
    systemConfig.passwordCheck = passwordCheckStr === 'true'
  } catch (error) {
    console.error('加载系统配置失败:', error)
  } finally {
    loading.value = false
  }
}

// 保存系统配置
const saveSystemConfig = async () => {
  saving.value = true
  try {
    const keys = CONFIG_KEYS.SYSTEM
    await saveConfigValue(keys.NAME, systemConfig.systemName, '系统名称', '系统设置')
    await saveConfigValue(keys.VERSION, systemConfig.systemVersion, '系统版本', '系统设置')
    await saveConfigValue(keys.DESCRIPTION, systemConfig.systemDescription, '系统描述', '系统设置')
    await saveConfigValue(keys.SESSION_TIMEOUT, systemConfig.sessionTimeout.toString(), '会话超时时间(分钟)', '系统设置')
    await saveConfigValue(keys.PASSWORD_CHECK, systemConfig.passwordCheck.toString(), '密码复杂度检查', '系统设置')
    
    message.success('系统配置保存成功')
  } catch (error) {
    message.error('保存失败：' + (error.message || '未知错误'))
  } finally {
    saving.value = false
  }
}

// 获取配置值（辅助函数）
const fetchConfigValue = async (configKey) => {
  return new Promise((resolve) => {
    getConfigValue(configKey, {
      onSuccess: (value) => {
        resolve(value)
      },
      onError: () => {
        resolve(null)
      }
    })
  })
}

// 保存配置值（辅助函数）
const saveConfigValue = async (configKey, configValue, description, configGroup) => {
  return new Promise((resolve, reject) => {
    // 先查询是否存在
    getSysConfigPage(
      {
        currentPage: 1,
        pageSize: 1,
        configKey: configKey
      },
      {
        onSuccess: (res) => {
          if (res.records && res.records.length > 0) {
            // 更新
            const config = res.records[0]
            updateSysConfig(config.id, {
              configValue,
              description,
              configGroup
            }, {
              onSuccess: () => resolve(),
              onError: (err) => reject(err)
            })
          } else {
            // 创建
            createSysConfig({
              configKey,
              configValue,
              description,
              configGroup
            }, {
              onSuccess: () => resolve(),
              onError: (err) => reject(err)
            })
          }
        },
        onError: (err) => reject(err)
      }
    )
  })
}

// 初始化
onMounted(() => {
  loadWechatConfig()
  loadFileConfig()
  loadSystemConfig()
})
</script>

<style scoped lang="scss">
.config-settings {
  .config-form {
    max-width: 800px;
    margin: 20px auto;
    padding: 20px;
  }

  .form-tip {
    color: #999;
    font-size: 12px;
    margin-top: 4px;
  }

  :deep(.ant-divider-inner-text) {
    font-weight: 600;
    color: #333;
  }

  :deep(.ant-form-item-label) {
    font-weight: 500;
  }
}
</style>
