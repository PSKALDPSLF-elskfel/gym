<template>
	<view class="page">
		<!-- 自定义导航栏 -->
		<mod-nav-bar title="添加体测数据" :back="true" />
		
		<view class="container">
			<view class="form-card">
				<!-- 基本信息 -->
				<view class="form-section">
					<view class="section-title">基本信息</view>
					
					<view class="form-item">
						<text class="label">身高 (cm)</text>
						<input 
							class="input" 
							type="digit" 
							v-model="formData.height" 
							placeholder="请输入身高"
							:disabled="submitting"
						/>
					</view>
					
					<view class="form-item">
						<text class="label">体重 (kg)</text>
						<input 
							class="input" 
							type="digit" 
							v-model="formData.weight" 
							placeholder="请输入体重"
							:disabled="submitting"
						/>
					</view>
					
					<view class="form-item">
						<text class="label">体脂率 (%)</text>
						<input 
							class="input" 
							type="digit" 
							v-model="formData.bodyFat" 
							placeholder="请输入体脂率"
							:disabled="submitting"
						/>
					</view>
				</view>
				
				<!-- 身体成分 -->
				<view class="form-section">
					<view class="section-title">身体成分</view>
					
					<view class="form-item">
						<text class="label">肌肉量 (kg)</text>
						<input 
							class="input" 
							type="digit" 
							v-model="formData.muscleMass" 
							placeholder="请输入肌肉量"
							:disabled="submitting"
						/>
					</view>
					
					<view class="form-item">
						<text class="label">内脏脂肪等级</text>
						<input 
							class="input" 
							type="number" 
							v-model="formData.visceralFat" 
							placeholder="请输入内脏脂肪等级"
							:disabled="submitting"
						/>
					</view>
					
					<view class="form-item">
						<text class="label">基础代谢 (kcal)</text>
						<input 
							class="input" 
							type="number" 
							v-model="formData.basalMetabolism" 
							placeholder="请输入基础代谢"
							:disabled="submitting"
						/>
					</view>
				</view>
				
				<!-- 测试信息 -->
				<view class="form-section">
					<view class="section-title">测试信息</view>
					
					<view class="form-item">
						<text class="label">测试日期</text>
						<picker 
							mode="date" 
							:value="formData.testDate"
							@change="onDateChange"
							:disabled="submitting"
						>
							<view class="picker-value">
								{{ formData.testDate || '请选择日期' }}
								<text class="arrow">›</text>
							</view>
						</picker>
					</view>
					
					<view class="form-item">
						<text class="label">测试时间</text>
						<picker 
							mode="time" 
							:value="formData.testTime"
							@change="onTimeChange"
							:disabled="submitting"
						>
							<view class="picker-value">
								{{ formData.testTime || '请选择时间' }}
								<text class="arrow">›</text>
							</view>
						</picker>
					</view>
					
					<view class="form-item">
						<text class="label">备注</text>
						<textarea 
							class="textarea" 
							v-model="formData.remark" 
							placeholder="请输入备注信息(可选)"
							maxlength="200"
							:disabled="submitting"
						/>
					</view>
				</view>
			</view>
			
			<!-- 提交按钮 -->
			<view class="button-group">
				<button class="btn-submit" @click="handleSubmit" :loading="submitting" :disabled="submitting">
					{{ submitting ? '提交中...' : '提交体测数据' }}
				</button>
			</view>
		</view>
	</view>
</template>

<script>
import { createBodyTest } from '@/apis/bodyTest.js'
import { getCurrentUser } from '@/apis/user.js'
import ModNavBar from '@/components/mod-nav-bar/mod-nav-bar.vue'

export default {
	components: {
		ModNavBar
	},
	data() {
		const today = new Date()
		const dateStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
		const timeStr = `${String(today.getHours()).padStart(2, '0')}:${String(today.getMinutes()).padStart(2, '0')}`
		
		return {
			userId: null,
			submitting: false,
			formData: {
				height: '',
				weight: '',
				bodyFat: '',
				muscleMass: '',
				visceralFat: '',
				basalMetabolism: '',
				testDate: dateStr,
				testTime: timeStr,
				remark: ''
			}
		}
	},
	onLoad() {
		this.initUser()
	},
	
	methods: {
		// 初始化用户信息
		async initUser() {
			try {
				const userRes = await getCurrentUser()
				this.userId = userRes.id
			} catch (error) {
				console.error('获取用户信息失败:', error)
				uni.showToast({
					title: '获取用户信息失败',
					icon: 'none'
				})
			}
		},
		
		// 日期改变
		onDateChange(e) {
			this.formData.testDate = e.detail.value
		},
		
		// 时间改变
		onTimeChange(e) {
			this.formData.testTime = e.detail.value
		},
		
		// 验证表单
		validateForm() {
			// 至少需要填写身高和体重
			if (!this.formData.height && !this.formData.weight) {
				uni.showToast({
					title: '请至少填写身高或体重',
					icon: 'none'
				})
				return false
			}
			
			// 验证数值范围
			if (this.formData.height && (this.formData.height < 50 || this.formData.height > 300)) {
				uni.showToast({
					title: '身高范围应在50-300cm之间',
					icon: 'none'
				})
				return false
			}
			
			if (this.formData.weight && (this.formData.weight < 20 || this.formData.weight > 500)) {
				uni.showToast({
					title: '体重范围应在20-500kg之间',
					icon: 'none'
				})
				return false
			}
			
			if (this.formData.bodyFat && (this.formData.bodyFat < 0 || this.formData.bodyFat > 100)) {
				uni.showToast({
					title: '体脂率范围应在0-100%之间',
					icon: 'none'
				})
				return false
			}
			
			if (this.formData.visceralFat && (this.formData.visceralFat < 0 || this.formData.visceralFat > 50)) {
				uni.showToast({
					title: '内脏脂肪等级范围应在0-50之间',
					icon: 'none'
				})
				return false
			}
			
			return true
		},
		
		// 提交表单
		async handleSubmit() {
			if (this.submitting) return
			
			if (!this.validateForm()) {
				return
			}
			
			if (!this.userId) {
				uni.showToast({
					title: '用户信息获取失败',
					icon: 'none'
				})
				return
			}
			
			try {
				this.submitting = true
				
				// 构建测试时间
				const testTimeStr = this.formData.testDate + ' ' + this.formData.testTime + ':00'
				
				// 构建提交数据
				const submitData = {
					userId: this.userId,
					height: this.formData.height ? parseFloat(this.formData.height) : null,
					weight: this.formData.weight ? parseFloat(this.formData.weight) : null,
					bodyFat: this.formData.bodyFat ? parseFloat(this.formData.bodyFat) : null,
					muscleMass: this.formData.muscleMass ? parseFloat(this.formData.muscleMass) : null,
					visceralFat: this.formData.visceralFat ? parseInt(this.formData.visceralFat) : null,
					basalMetabolism: this.formData.basalMetabolism ? parseInt(this.formData.basalMetabolism) : null,
					testTime: testTimeStr,
					remark: this.formData.remark || null
				}
				
				// 移除空值
				Object.keys(submitData).forEach(key => {
					if (submitData[key] === null || submitData[key] === '') {
						delete submitData[key]
					}
				})
				
				await createBodyTest(submitData)
				
				uni.showToast({
					title: '添加成功',
					icon: 'success'
				})
				
				// 延迟返回，让用户看到成功提示
				setTimeout(() => {
					uni.navigateBack()
				}, 1500)
			} catch (error) {
				console.error('添加体测数据失败:', error)
				uni.showToast({
					title: error.message || '添加失败',
					icon: 'none'
				})
			} finally {
				this.submitting = false
			}
		}
	}
}
</script>

<style scoped>
.page {
	min-height: 100vh;
	background: #f5f5f5;
	padding-bottom: 120rpx;
}

.container {
	padding: 20rpx;
}

.form-card {
	background: white;
	border-radius: 16rpx;
	overflow: hidden;
}

.form-section {
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.form-section:last-child {
	border-bottom: none;
}

.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 30rpx;
}

.form-item {
	margin-bottom: 30rpx;
}

.form-item:last-child {
	margin-bottom: 0;
}

.label {
	display: block;
	font-size: 28rpx;
	color: #666;
	margin-bottom: 15rpx;
}

.input {
	width: 100%;
	height: 80rpx;
	padding: 0 20rpx;
	font-size: 28rpx;
	background: #f8f8f8;
	border-radius: 8rpx;
	box-sizing: border-box;
}

.textarea {
	width: 100%;
	min-height: 150rpx;
	padding: 15rpx 20rpx;
	font-size: 28rpx;
	background: #f8f8f8;
	border-radius: 8rpx;
	box-sizing: border-box;
}

.picker-value {
	display: flex;
	align-items: center;
	justify-content: space-between;
	height: 80rpx;
	padding: 0 20rpx;
	font-size: 28rpx;
	color: #333;
	background: #f8f8f8;
	border-radius: 8rpx;
}

.picker-value text {
	color: #999;
}

.arrow {
	font-size: 40rpx;
	color: #ccc;
}

.button-group {
	padding: 30rpx 20rpx;
}

.btn-submit {
	width: 100%;
	height: 90rpx;
	line-height: 90rpx;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: white;
	font-size: 32rpx;
	font-weight: bold;
	border-radius: 45rpx;
	border: none;
}

.btn-submit[disabled] {
	opacity: 0.6;
}
</style>
