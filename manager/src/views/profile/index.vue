<template>
  <div class="profile-container">
    <a-card class="profile-card">
      <template #title>
        <div class="card-header">
          <h2>个人中心</h2>
        </div>
      </template>

      <a-tabs v-model:activeKey="activeTab">
        <!-- 基本信息 Tab -->
        <a-tab-pane key="basic" tab="基本信息">
          <div class="profile-info">
            <div class="avatar-container">
              <a-avatar :size="100" :src="avatarUrl">
                {{ userForm.nickname?.charAt(0) || userForm.username?.charAt(0) || 'U' }}
              </a-avatar>
              <a-upload
                class="avatar-uploader"
                :action="uploadAction"
                :show-upload-list="false"
                :custom-request="customUploadAvatar"
                :before-upload="beforeAvatarUpload"
              >
                <a-button size="small" type="primary">更换头像</a-button>
              </a-upload>
            </div>

            <div class="info-form">
              <a-form
                ref="userFormRef"
                :model="userForm"
                :rules="rules"
                :label-col="{ span: 6 }"
                :wrapper-col="{ span: 18 }"
              >
                <a-form-item label="用户名" name="username">
                  <a-input v-model:value="userForm.username" disabled />
                </a-form-item>

                <a-form-item label="昵称" name="nickname">
                  <a-input v-model:value="userForm.nickname" />
                </a-form-item>

                <a-form-item label="电子邮箱" name="email">
                  <a-input v-model:value="userForm.email" />
                </a-form-item>

                <a-form-item label="手机号码" name="phone">
                  <a-input v-model:value="userForm.phone" />
                </a-form-item>

                <a-form-item :wrapper-col="{ offset: 6, span: 18 }">
                  <a-button type="primary" @click="submitUserInfo">
                    保存修改
                  </a-button>
                </a-form-item>
              </a-form>
            </div>
          </div>
        </a-tab-pane>

        <!-- 修改密码 Tab -->
        <a-tab-pane key="password" tab="修改密码">
          <a-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            :label-col="{ span: 6 }"
            :wrapper-col="{ span: 18 }"
            style="max-width: 500px; margin: 0 auto"
          >
            <a-form-item label="旧密码" name="oldPassword">
              <a-input-password
                v-model:value="passwordForm.oldPassword"
                placeholder="请输入旧密码"
              />
            </a-form-item>

            <a-form-item label="新密码" name="newPassword">
              <a-input-password
                v-model:value="passwordForm.newPassword"
                placeholder="请输入新密码"
              />
            </a-form-item>

            <a-form-item label="确认新密码" name="confirmPassword">
              <a-input-password
                v-model:value="passwordForm.confirmPassword"
                placeholder="请再次输入新密码"
              />
            </a-form-item>

            <a-form-item :wrapper-col="{ offset: 6, span: 18 }">
              <a-button type="primary" @click="submitPassword">
                修改密码
              </a-button>
            </a-form-item>
          </a-form>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from "vue";
import { message, Modal } from "ant-design-vue";
import { useUserStore } from "@/store/user";
import request from "@/utils/request";
import { getCurrentUser, updateUser, updatePassword } from '@/api/user';

const uploadAction = "#";
const userStore = useUserStore();
const activeTab = ref("basic");

// 表单引用
const userFormRef = ref(null);
const passwordFormRef = ref(null);

// 用户表单数据
const userForm = reactive({
  id: "",
  username: "",
  nickname: "",
  email: "",
  phone: "",
  avatar: "",
});

// 头像地址
const avatarUrl = computed(() => {
  return userForm.avatar;
});

// 密码表单数据
const passwordForm = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

// 表单校验规则
const rules = {
  nickname: [{ required: false, message: "请输入昵称", trigger: "blur" }],
  email: [
    { required: true, message: "请输入邮箱地址", trigger: "blur" },
    {
      type: "email",
      message: "请输入正确的邮箱地址",
      trigger: ["blur", "change"],
    },
  ],
  phone: [
    { required: false, trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号码",
      trigger: ["blur", "change"],
    },
  ],
};

// 密码表单校验规则
const passwordRules = {
  oldPassword: [
    { required: true, message: "请输入旧密码", trigger: "blur" },
    { min: 6, message: "密码长度不能小于6个字符", trigger: "blur" },
  ],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 6, message: "密码长度不能小于6个字符", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请再次输入新密码", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error("两次输入的密码不一致"));
        } else {
          callback();
        }
      },
      trigger: ["blur", "change"],
    },
  ],
};

// 获取用户信息
const getUserInfo = async () => {
  try {
    // 从后端获取最新用户信息
    const data = await getCurrentUser();
    
    // 更新store
    userStore.updateUserInfo(data);
    
    // 更新表单数据
    userForm.id = data.id || "";
    userForm.username = data.username || "";
    userForm.nickname = data.nickname || "";
    userForm.email = data.email || "";
    userForm.phone = data.phone || "";
    userForm.avatar = data.avatar || "";

    console.log("用户信息加载成功:", userForm);
  } catch (error) {
    console.error("获取用户信息失败", error);
    message.error("获取用户信息失败");
  }
};

// 上传头像前的校验
const beforeAvatarUpload = (file) => {
  const isJPG = file.type === "image/jpeg";
  const isPNG = file.type === "image/png";
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG && !isPNG) {
    message.error("头像只能是 JPG 或 PNG 格式!");
    return false;
  }
  if (!isLt2M) {
    message.error("头像大小不能超过 2MB!");
    return false;
  }
  return true;
};

// 自定义头像上传方法（使用策略C：直接业务绑定上传）
const customUploadAvatar = async (options) => {
  try {
    const { file } = options;

    // 创建 FormData 对象
    const formData = new FormData();
    formData.append("file", file);
    formData.append("businessType", "USER_AVATAR");
    formData.append("businessId", userForm.id.toString());
    formData.append("businessField", "avatar");
    formData.append("replaceOld", "true"); // 替换旧头像

    // 发送上传请求
    const response = await request.post("/file/upload", formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: `Bearer ${userStore.token}`,
      }
    });

    // 更新用户头像路径
    const avatarPath = response.filePath || response.path;
    userForm.avatar = avatarPath;

    // 更新用户信息到后端
    await updateUserAvatar(avatarPath);

    // 通知上传成功
    options.onSuccess(response);
    message.success("头像上传成功");
  } catch (error) {
    options.onError(error);
    console.error("头像上传过程发生错误:", error);
    message.error("头像上传失败: " + (error.message || "未知错误"));
  }
};

// 更新用户头像信息
const updateUserAvatar = async (avatarPath) => {
  try {
    // 更新用户信息到后端
    const response = await updateUser(userForm.id, { avatar: avatarPath });
    
    // 更新本地store
    userStore.updateUserInfo(response);
    
    console.log("头像信息已保存到后端");
  } catch (error) {
    console.error("头像信息保存失败", error);
    message.error("头像信息保存失败");
    throw error;
  }
};

// 提交用户信息更新
const submitUserInfo = async () => {
  if (!userFormRef.value) return;

  try {
    // 表单验证
    await userFormRef.value.validate();

    // 更新用户信息到后端
    const response = await updateUser(userForm.id, {
      nickname: userForm.nickname,
      email: userForm.email,
      phone: userForm.phone,
    });
    
    // 更新本地store
    userStore.updateUserInfo(response);
    
    message.success("个人信息更新成功!");

  } catch (error) {
    console.error("保存个人信息失败", error);
    message.error("保存个人信息失败: " + (error.message || "未知错误"));
  }
};

// 提交密码修改
const submitPassword = async () => {
  if (!passwordFormRef.value) return;

  try {
    // 表单验证
    await passwordFormRef.value.validate();

    // 调用后端修改密码接口
    await updatePassword(userForm.id, {
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword,
    }, {
      successMsg: "密码修改成功，即将跳转到登录页",
      showDefaultMsg: true
    });
    
    // 清空表单
    passwordForm.oldPassword = "";
    passwordForm.newPassword = "";
    passwordForm.confirmPassword = "";

    // 显示提示信息（不可取消）
    Modal.info({
      title: "密码修改成功",
      content: "为了您的账户安全，请重新登录",
      okText: "确定",
      onOk: async () => {
        // 清除用户信息并跳转到登录页
        await userStore.logout();
        window.location.href = "/auth/login";
      }
    });

    // 3秒后自动跳转（即使用户不点击确定按钮）
    setTimeout(async () => {
      await userStore.logout();
      window.location.href = "/auth/login";
    }, 3000);

  } catch (error) {
    console.error("密码修改失败", error);
    if (!error.errorFields) {
      message.error(error.message || "密码修改失败");
    }
  }
};

// 监听用户表单数据变化
watch(
  userForm,
  (newVal) => {
    console.log("用户表单数据变化:", newVal);
  },
  { deep: true }
);

// 组件挂载时获取用户信息
onMounted(() => {
  getUserInfo();
});
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  max-width: 1000px;
  margin: 0 auto;
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

@media (min-width: 768px) {
  .profile-info {
    flex-direction: row;
  }
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.avatar-uploader {
  text-align: center;
  margin-top: 10px;
}

.info-form {
  flex: 1;
}

.ant-tabs {
  margin-top: 20px;
}
</style> 