<template>
  <div class="course-management">
    <a-card title="课程管理" :bordered="false">
      <!-- 搜索栏 -->
      <a-form layout="inline" :model="searchForm" class="search-form">
        <a-form-item label="课程名称">
          <a-input
            v-model:value="searchForm.name"
            placeholder="请输入课程名称"
            allow-clear
            style="width: 200px"
            @pressEnter="handleSearch"
          />
        </a-form-item>
        <a-form-item label="课程分类">
          <a-select
            v-model:value="searchForm.categoryId"
            placeholder="请选择分类"
            allow-clear
            style="width: 150px"
          >
            <a-select-option v-for="category in categoryList" :key="category.id" :value="category.id">
              {{ category.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="searchForm.status"
            placeholder="请选择状态"
            allow-clear
            style="width: 120px"
          >
            <a-select-option :value="1">上架</a-select-option>
            <a-select-option :value="0">下架</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">
            <template #icon><SearchOutlined /></template>
            查询
          </a-button>
          <a-button style="margin-left: 8px" @click="handleReset">
            <template #icon><ReloadOutlined /></template>
            重置
          </a-button>
        </a-form-item>
      </a-form>

      <!-- 操作按钮 -->
      <div class="table-operations">
        <a-button type="primary" @click="handleAdd">
          <template #icon><PlusOutlined /></template>
          新增课程
        </a-button>
      </div>

      <!-- 表格 -->
      <a-table
        :columns="columns"
        :data-source="tableData"
        :pagination="pagination"
        :loading="loading"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'coverImage'">
            <a-image
              v-if="record.coverImage"
              :src="getImageUrl(record.coverImage)"
              :width="80"
              :height="60"
              style="object-fit: cover"
              :fallback="'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iODAiIGhlaWdodD0iNjAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdGg9IjgwIiBoZWlnaHQ9IjYwIiBmaWxsPSIjZjBmMGYwIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtc2l6ZT0iMTIiIGZpbGw9IiM5OTkiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj7ml6Dlm77niYc8L3RleHQ+PC9zdmc+'"
              @error="handleImageError(record)"
            />
            <span v-else style="color: #999">-</span>
          </template>
          <template v-else-if="column.key === 'price'">
            ¥{{ record.price }}
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.status === 1 ? 'success' : 'default'">
              {{ record.statusDisplayName }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleSchedule(record)">
                时间安排
              </a-button>
              <a-button type="link" size="small" @click="handleEdit(record)">
                编辑
              </a-button>
              <a-button
                type="link"
                size="small"
                @click="handleToggleStatus(record)"
              >
                {{ record.status === 1 ? '下架' : '上架' }}
              </a-button>
              <a-popconfirm
                title="确定要删除这个课程吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="handleDelete(record)"
              >
                <a-button type="link" size="small" danger>
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 新增/编辑对话框 -->
    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      :width="800"
      @ok="handleModalOk"
      @cancel="handleModalCancel"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="课程名称" name="name">
          <a-input
            v-model:value="formData.name"
            placeholder="请输入课程名称"
            :maxlength="100"
          />
        </a-form-item>
        <a-form-item label="课程分类" name="categoryId">
          <a-select
            v-model:value="formData.categoryId"
            placeholder="请选择课程分类"
            allow-clear
          >
            <a-select-option v-for="category in categoryList" :key="category.id" :value="category.id">
              {{ category.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="负责教练" name="coachId">
          <a-select
            v-model:value="formData.coachId"
            placeholder="请选择负责教练"
            allow-clear
            show-search
            :filter-option="filterCoachOption"
          >
            <a-select-option v-for="coach in coachList" :key="coach.id" :value="coach.id">
              {{ coach.nickname || coach.username }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="课程描述" name="description">
          <a-textarea
            v-model:value="formData.description"
            placeholder="请输入课程描述"
            :rows="4"
            :maxlength="500"
          />
        </a-form-item>
        <a-form-item label="封面图片" name="coverImage">
          <a-upload
            v-model:file-list="fileList"
            list-type="picture-card"
            :max-count="1"
            :before-upload="beforeUpload"
            :custom-request="handleUpload"
            @remove="handleRemove"
          >
            <div v-if="fileList.length < 1">
              <PlusOutlined />
              <div style="margin-top: 8px">上传</div>
            </div>
          </a-upload>
        </a-form-item>
        <a-form-item label="课程时长" name="duration">
          <a-input-number
            v-model:value="formData.duration"
            placeholder="请输入课程时长"
            :min="1"
            :max="1440"
            style="width: 100%"
            addon-after="分钟"
          />
        </a-form-item>
        <a-form-item label="最大参与人数" name="maxParticipants">
          <a-input-number
            v-model:value="formData.maxParticipants"
            placeholder="请输入最大参与人数"
            :min="1"
            :max="1000"
            style="width: 100%"
            addon-after="人"
          />
        </a-form-item>
        <a-form-item label="课程价格" name="price">
          <a-input-number
            v-model:value="formData.price"
            placeholder="请输入课程价格"
            :min="0"
            :max="99999.99"
            :precision="2"
            style="width: 100%"
            addon-before="¥"
          />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="formData.status">
            <a-radio :value="1">上架</a-radio>
            <a-radio :value="0">下架</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined
} from '@ant-design/icons-vue'
import {
  getCoursePage,
  createCourse,
  updateCourse,
  deleteCourse,
  updateCourseStatus
} from '@/api/CourseApi'
import {
  uploadBusinessFile
} from '@/api/FileApi'
import { getImageUrl } from '@/utils/fileUtils'
import { getAllEnabledCategories } from '@/api/CourseCategoryApi'
import { getAllActiveCoaches } from '@/api/CoachApi'

const router = useRouter()

// 生成UUID的函数
function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0
    const v = c === 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}

// 搜索表单
const searchForm = reactive({
  name: '',
  categoryId: undefined,
  status: undefined
})

// 分类列表
const categoryList = ref([])
// 教练列表
const coachList = ref([])

// 表格列定义
const columns = [
  {
    title: '课程名称',
    dataIndex: 'name',
    key: 'name',
    width: 150
  },
  {
    title: '课程分类',
    dataIndex: 'categoryName',
    key: 'categoryName',
    width: 120
  },
  {
    title: '负责教练',
    dataIndex: 'coachName',
    key: 'coachName',
    width: 120
  },
  {
    title: '封面图片',
    dataIndex: 'coverImage',
    key: 'coverImage',
    width: 120
  },
  {
    title: '时长(分钟)',
    dataIndex: 'duration',
    key: 'duration',
    width: 100
  },
  {
    title: '最大人数',
    dataIndex: 'maxParticipants',
    key: 'maxParticipants',
    width: 100
  },
  {
    title: '价格',
    dataIndex: 'price',
    key: 'price',
    width: 100
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 80
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 200
  }
]

// 表格数据
const tableData = ref([])
const loading = ref(false)
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条`
})

// 对话框相关
const modalVisible = ref(false)
const modalTitle = ref('')
const isEdit = ref(false)
const formRef = ref(null)
const formData = reactive({
  id: '',
  name: '',
  categoryId: undefined,
  coachId: undefined,
  description: '',
  coverImage: '',
  duration: null,
  maxParticipants: null,
  price: null,
  status: 1
})

// 文件上传相关
const fileList = ref([])

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { max: 100, message: '课程名称长度不能超过100个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择课程分类', trigger: 'change' }
  ],
  coachId: [
    { required: true, message: '请选择负责教练', trigger: 'change' }
  ],
  duration: [
    { required: true, message: '请输入课程时长', trigger: 'blur' }
  ],
  maxParticipants: [
    { required: true, message: '请输入最大参与人数', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入课程价格', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取课程列表
const fetchCourses = () => {
  loading.value = true
  getCoursePage(
    {
      current: pagination.current,
      size: pagination.pageSize,
      name: searchForm.name || undefined,
      status: searchForm.status
    },
    {
      onSuccess: (res) => {
        tableData.value = res.records || []
        pagination.total = res.total || 0
        loading.value = false
      },
      onError: () => {
        loading.value = false
      }
    }
  )
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchCourses()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.categoryId = undefined
  searchForm.status = undefined
  pagination.current = 1
  fetchCourses()
}

// 表格分页变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchCourses()
}

// 时间安排
const handleSchedule = (record) => {
  router.push(`/back/course/schedule/${record.id}`)
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  modalTitle.value = '新增课程'
  resetForm()
  // 预生成UUID
  formData.id = generateUUID()
  modalVisible.value = true
}

// 编辑
const handleEdit = (record) => {
  isEdit.value = true
  modalTitle.value = '编辑课程'
  resetForm()
  Object.assign(formData, {
    id: record.id,
    name: record.name,
    categoryId: record.categoryId,
    coachId: record.coachId,
    description: record.description,
    coverImage: record.coverImage,
    duration: record.duration,
    maxParticipants: record.maxParticipants,
    price: record.price,
    status: record.status
  })
  
  // 如果有封面图片，设置fileList
  if (record.coverImage) {
    fileList.value = [{
      uid: '-1',
      name: 'cover.jpg',
      status: 'done',
      url: getImageUrl(record.coverImage)
    }]
  }
  
  modalVisible.value = true
}

// 删除
const handleDelete = (record) => {
  deleteCourse(record.id, {
    successMsg: '删除成功',
    onSuccess: () => {
      fetchCourses()
    }
  })
}

// 切换状态
const handleToggleStatus = (record) => {
  const newStatus = record.status === 1 ? 0 : 1
  updateCourseStatus(record.id, newStatus, {
    successMsg: `${newStatus === 1 ? '上架' : '下架'}成功`,
    onSuccess: () => {
      fetchCourses()
    }
  })
}

// 对话框确定
const handleModalOk = async () => {
  try {
    await formRef.value.validate()
    
    const params = {
      ...formData
    }
    
    if (isEdit.value) {
      updateCourse(formData.id, params, {
        successMsg: '更新成功',
        onSuccess: () => {
          modalVisible.value = false
          fetchCourses()
        }
      })
    } else {
      createCourse(params, {
        successMsg: '创建成功',
        onSuccess: () => {
          modalVisible.value = false
          fetchCourses()
        }
      })
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 对话框取消
const handleModalCancel = () => {
  modalVisible.value = false
  resetForm()
}

// 重置表单
const resetForm = () => {
  formData.id = ''
  formData.name = ''
  formData.categoryId = undefined
  formData.coachId = undefined
  formData.description = ''
  formData.coverImage = ''
  formData.duration = null
  formData.maxParticipants = null
  formData.price = null
  formData.status = 1
  fileList.value = []
  formRef.value?.clearValidate()
}

// 文件上传前验证
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件！')
    return false
  }
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    message.error('图片大小不能超过5MB！')
    return false
  }
  return true
}

// 自定义上传
const handleUpload = async ({ file, onSuccess, onError }) => {
  try {
    // 使用uploadBusinessFile方法
    // 新增模式：replaceOld=false
    // 编辑模式：replaceOld=true（替换旧文件）
    uploadBusinessFile(
      file,
      'COURSE',
      formData.id,
      'cover_image',
      isEdit.value, // replaceOld参数：编辑时为true，新增时为false
      {
        showDefaultMsg: false,
        onSuccess: (data) => {
          // 后端返回的是FileInfoDTO对象，字段名是filePath
          console.log('文件上传成功，返回数据:', data)
          formData.coverImage = data.filePath
          // 注意：编辑模式下，后端会自动删除旧文件（replaceOld=true）
          onSuccess()
          message.success('上传成功')
        },
        onError: (error) => {
          console.error('上传失败:', error)
          onError(error)
          message.error('上传失败')
        }
      }
    )
  } catch (error) {
    console.error('上传异常:', error)
    onError(error)
    message.error('上传失败')
  }
}

// 移除文件
const handleRemove = () => {
  // 只清空前端显示，不调用删除API
  // 原因：
  // 1. 如果用户重新上传文件，后端会自动删除旧文件（replaceOld=true）
  // 2. 如果用户直接提交表单（coverImage为空），后端应该处理删除逻辑
  formData.coverImage = ''
  fileList.value = []
  console.log('已清空封面图片')
}

// 获取分类列表
const fetchCategories = () => {
  getAllEnabledCategories({
    onSuccess: (data) => {
      categoryList.value = data || []
    },
    onError: () => {
      message.error('获取分类列表失败')
    }
  })
}

// 获取教练列表
const fetchCoaches = () => {
  getAllActiveCoaches({
    onSuccess: (data) => {
      coachList.value = data || []
    },
    onError: () => {
      message.error('获取教练列表失败')
    }
  })
}

// 教练搜索过滤
const filterCoachOption = (input, option) => {
  return option.children[0].children.toLowerCase().indexOf(input.toLowerCase()) >= 0
}

// 组件挂载时获取数据
onMounted(() => {
  fetchCourses()
  fetchCategories()
  fetchCoaches()
})

// 图片加载失败处理
const handleImageError = (record) => {
  console.error('图片加载失败:', {
    courseId: record.id,
    courseName: record.name,
    coverImage: record.coverImage,
    fullUrl: getImageUrl(record.coverImage)
  })
}
</script>

<style scoped lang="scss">
.course-management {
  .search-form {
    margin-bottom: 16px;
  }

  .table-operations {
    margin-bottom: 16px;
  }
}
</style>
