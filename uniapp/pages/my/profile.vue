<template>
  <view class="profile-page">
    <mod-nav-bar title="个人信息" title-color="#333"></mod-nav-bar>
    
    <view class="profile-container">
      <!-- 头像部分 -->
      <view class="avatar-section">
        <view class="avatar-wrapper" @click="chooseAvatar">
          <image v-if="userForm.avatar" class="avatar" :src="getAvatarUrl(userForm.avatar)" mode="aspectFill"></image>
          <view v-else class="avatar-placeholder">
            <text class="fa fa-user-circle icon"></text>
          </view>
          <view class="camera-icon">
            <text class="fa fa-camera"></text>
          </view>
        </view>
        <text class="avatar-tip">点击更换头像</text>
      </view>

      <!-- 表单部分 -->
      <view class="form-section">
        <view class="form-item">
          <text class="label">用户名</text>
          <input class="input disabled" v-model="userForm.username" disabled />
        </view>

        <view class="form-item">
          <text class="label">昵称</text>
          <input class="input" v-model="userForm.nickname" placeholder="请输入昵称" />
        </view>

        <view class="form-item">
          <text class="label">邮箱</text>
          <input class="input" v-model="userForm.email" placeholder="请输入邮箱" />
        </view>

        <view class="form-item">
          <text class="label">手机号</text>
          <input class="input" v-model="userForm.phone" placeholder="请输入手机号" />
        </view>

        <view class="form-item">
          <text class="label">用户类型</text>
          <input class="input disabled" :value="getUserTypeLabel(userForm.userType)" disabled />
        </view>
      </view>

      <!-- 按钮组 -->
      <view class="button-group">
        <button class="save-btn" @click="handleSave" :loading="loading">
          <text class="fa fa-save"></text>
          保存修改
        </button>
        
        <button class="password-btn" @click="goToChangePassword">
          <text class="fa fa-key"></text>
          修改密码
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user.js'
import { getCurrentUser, updateUser } from '@/apis/user.js'
import { getFileUrl } from '@/utils/fileUtils.js'

const userStore = useUserStore()
const baseAPI = 'http://localhost:8888/api'

// 数据
const userForm = ref({
  id: '',
  username: '',
  nickname: '',
  email: '',
  phone: '',
  avatar: '',
  userType: ''
})

const loading = ref(false)

/**
 * 获取用户信息
 */
const getUserInfo = () => {
  getCurrentUser({
    showDefaultMsg: false,
    onSuccess: (data) => {
      userForm.value = {
        id: data.id || '',
        username: data.username || '',
        nickname: data.nickname || '',
        email: data.email || '',
        phone: data.phone || '',
        avatar: data.avatar || '',
        userType: data.userType || ''
      }
    },
    onError: (error) => {
      console.error('获取用户信息失败:', error)
      uni.showToast({
        title: '获取用户信息失败',
        icon: 'none'
      })
    }
  })
}

/**
 * 选择头像
 */
const chooseAvatar = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      uploadAvatar(res.tempFilePaths[0])
    }
  })
}

/**
 * 上传头像
 */
const uploadAvatar = async (filePath) => {
  uni.showLoading({
    title: '上传中...'
  })

  try {
    const uploadResult = await new Promise((resolve, reject) => {
      uni.uploadFile({
        url: `${baseAPI}/file/upload`,
        filePath: filePath,
        name: 'file',
        header: {
          'Authorization': `Bearer ${userStore.token}`
        },
        formData: {
          businessType: 'USER_AVATAR',
          businessId: userForm.value.id,
          businessField: 'avatar',
          replaceOld: 'true'
        },
        success: (res) => {
          if (res.statusCode === 200) {
            const data = JSON.parse(res.data)
            if (data.code === "200") {
              resolve(data.data)
            } else {
              reject(new Error(data.msg || '上传失败'))
            }
          } else {
            reject(new Error('上传失败'))
          }
        },
        fail: (err) => {
          reject(err)
        }
      })
    })

    // 更新头像路径
    userForm.value.avatar = uploadResult.filePath || uploadResult.path
    
    // 更新用户信息到后端
    await updateUser(userForm.value.id, {
      avatar: userForm.value.avatar
    }, {
      successMsg: '头像上传成功',
      showDefaultMsg: true
    })

    // 更新store
    userStore.updateUserInfo({ avatar: userForm.value.avatar })

  } catch (error) {
    console.error('头像上传失败:', error)
    uni.showToast({
      title: error.message || '头像上传失败',
      icon: 'none'
    })
  } finally {
    uni.hideLoading()
  }
}

/**
 * 保存修改
 */
const handleSave = () => {
  // 表单验证
  if (!userForm.value.email) {
    uni.showToast({
      title: '请输入邮箱',
      icon: 'none'
    })
    return
  }

  const emailRegex = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/
  if (!emailRegex.test(userForm.value.email)) {
    uni.showToast({
      title: '邮箱格式不正确',
      icon: 'none'
    })
    return
  }

  if (userForm.value.phone) {
    const phoneRegex = /^1[3-9]\d{9}$/
    if (!phoneRegex.test(userForm.value.phone)) {
      uni.showToast({
        title: '手机号格式不正确',
        icon: 'none'
      })
      return
    }
  }

  loading.value = true

  // 更新用户信息
  updateUser(userForm.value.id, {
    nickname: userForm.value.nickname,
    email: userForm.value.email,
    phone: userForm.value.phone
  }, {
    successMsg: '保存成功',
    onSuccess: (data) => {
      // 更新store
      userStore.updateUserInfo({
        nickname: userForm.value.nickname,
        email: userForm.value.email,
        phone: userForm.value.phone
      })
      loading.value = false
    },
    onError: (error) => {
      console.error('保存失败:', error)
      loading.value = false
    }
  })
}

/**
 * 跳转到修改密码页面
 */
const goToChangePassword = () => {
  uni.navigateTo({
    url: '/pages/my/change-password'
  })
}

/**
 * 获取头像URL
 * 使用工具函数转换文件路径为完整URL
 */
const getAvatarUrl = (avatar) => {
  if (!avatar) return ''
  // 使用工具函数转换路径，解决小程序端无法访问后端文件的问题
  return getFileUrl(avatar)
}

/**
 * 获取用户类型标签
 */
const getUserTypeLabel = (userType) => {
  const typeMap = {
    'ADMIN': '管理员',
    'USER': '普通用户'
  }
  return typeMap[userType] || '未知'
}

// 页面加载时获取用户信息
onMounted(() => {
  getUserInfo()
})
</script>

<style lang="scss" scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.profile-container {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 60rpx 40rpx;
  margin: 40rpx;
}

.avatar-section {
  text-align: center;
  margin-bottom: 60rpx;

  .avatar-wrapper {
    position: relative;
    width: 160rpx;
    height: 160rpx;
    margin: 0 auto 20rpx;

    .avatar {
      width: 100%;
      height: 100%;
      border-radius: 50%;
    }

    .avatar-placeholder {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;

      .icon {
        font-size: 160rpx;
        color: #667eea;
      }
    }

    .camera-icon {
      position: absolute;
      right: 0;
      bottom: 0;
      width: 48rpx;
      height: 48rpx;
      background: #667eea;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;

      .fa {
        font-size: 24rpx;
        color: #ffffff;
      }
    }
  }

  .avatar-tip {
    display: block;
    font-size: 24rpx;
    color: #999;
  }
}

.form-section {
  .form-item {
    display: flex;
    align-items: center;
    padding: 24rpx 0;
    border-bottom: 1rpx solid #f0f0f0;

    .label {
      width: 140rpx;
      font-size: 28rpx;
      color: #333;
    }

    .input {
      flex: 1;
      font-size: 28rpx;
      color: #333;
      text-align: right;

      &.disabled {
        color: #999;
      }
    }

    .picker {
      flex: 1;
      font-size: 28rpx;
      color: #333;
      text-align: right;
    }
  }
}

.button-group {
  margin-top: 60rpx;

  .save-btn, .password-btn {
    width: 100%;
    height: 88rpx;
    font-size: 32rpx;
    border-radius: 12rpx;
    border: none;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12rpx;
    margin-bottom: 24rpx;

    .fa {
      font-size: 28rpx;
    }

    &::after {
      border: none;
    }
  }

  .save-btn {
    background: #667eea;
    color: #ffffff;
  }

  .password-btn {
    background: #f5f5f5;
    color: #333;
  }
}
</style>
