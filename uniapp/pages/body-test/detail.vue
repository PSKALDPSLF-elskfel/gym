<template>
	<view class="page">
		<!-- 自定义导航栏 -->
		<mod-nav-bar title="体测详情" :back="true" />
		
		<view class="container">
			<!-- 加载状态 -->
			<view v-if="loading" class="loading-container">
				<uni-load-more status="loading" />
			</view>
			
			<!-- 详细信息 -->
			<view v-else-if="bodyTestData" class="content">
				<!-- 测试时间 -->
				<view class="time-banner">
					<text class="time-text">{{ formatDateTime(bodyTestData.testTime) }}</text>
				</view>
				
				<!-- 基本指标 -->
				<view class="card">
					<view class="card-title">基本指标</view>
					<view class="metrics-grid">
						<view class="metric-item">
							<text class="metric-label">身高</text>
							<text class="metric-value">{{ bodyTestData.height || '-' }}</text>
							<text class="metric-unit">cm</text>
						</view>
						<view class="metric-item">
							<text class="metric-label">体重</text>
							<text class="metric-value">{{ bodyTestData.weight || '-' }}</text>
							<text class="metric-unit">kg</text>
						</view>
						<view class="metric-item">
							<text class="metric-label">BMI</text>
							<text class="metric-value" :class="getBMIClass(bodyTestData.bmi)">
								{{ bodyTestData.bmi ? bodyTestData.bmi.toFixed(1) : '-' }}
							</text>
							<text class="metric-unit">{{ getBMIStatus(bodyTestData.bmi) }}</text>
						</view>
						<view class="metric-item">
							<text class="metric-label">体脂率</text>
							<text class="metric-value">{{ bodyTestData.bodyFat || '-' }}</text>
							<text class="metric-unit">%</text>
						</view>
					</view>
				</view>
				
				<!-- 身体成分 -->
				<view class="card">
					<view class="card-title">身体成分</view>
					<view class="composition-grid">
						<view class="composition-item">
							<view class="composition-icon">💪</view>
							<text class="composition-label">肌肉量</text>
							<text class="composition-value">{{ bodyTestData.muscleMass || '-' }} kg</text>
						</view>
						<view class="composition-item">
							<view class="composition-icon">🫀</view>
							<text class="composition-label">内脏脂肪</text>
							<text class="composition-value" :class="getVisceralFatClass(bodyTestData.visceralFat)">
								等级 {{ bodyTestData.visceralFat || '-' }}
							</text>
						</view>
						<view class="composition-item">
							<view class="composition-icon">🔥</view>
							<text class="composition-label">基础代谢</text>
							<text class="composition-value">{{ bodyTestData.basalMetabolism || '-' }} kcal</text>
						</view>
					</view>
				</view>
				
				<!-- 健康提示 -->
				<view class="card">
					<view class="card-title">健康提示</view>
					<view class="health-tips">
						<view class="tip-item" v-for="(tip, index) in healthTips" :key="index">
							<text class="tip-icon">{{ tip.icon }}</text>
							<text class="tip-text">{{ tip.text }}</text>
						</view>
					</view>
				</view>
				
				<!-- 备注 -->
				<view v-if="bodyTestData.remark" class="card">
					<view class="card-title">备注</view>
					<text class="remark-text">{{ bodyTestData.remark }}</text>
				</view>
				
				<!-- 测试人员 -->
				<view v-if="bodyTestData.testerName" class="card">
					<view class="card-title">测试人员</view>
					<text class="tester-name">{{ bodyTestData.testerName }}</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { getBodyTestById } from '@/apis/bodyTest.js'
import ModNavBar from '@/components/mod-nav-bar/mod-nav-bar.vue'

export default {
	components: {
		ModNavBar
	},
	data() {
		return {
			loading: true,
			bodyTestData: null,
			bodyTestId: null
		}
	},
	computed: {
		// 健康提示
		healthTips() {
			if (!this.bodyTestData) return []
			
			const tips = []
			const bmi = this.bodyTestData.bmi
			const bodyFat = this.bodyTestData.bodyFat
			const visceralFat = this.bodyTestData.visceralFat
			
			// BMI相关提示
			if (bmi) {
				if (bmi < 18.5) {
					tips.push({ icon: '⚠️', text: 'BMI偏低，建议增加营养摄入，适当进行力量训练' })
				} else if (bmi >= 24 && bmi < 28) {
					tips.push({ icon: '💡', text: 'BMI略高，建议控制饮食，增加有氧运动' })
				} else if (bmi >= 28) {
					tips.push({ icon: '⚠️', text: 'BMI超标，建议咨询专业教练，制定减重计划' })
				} else {
					tips.push({ icon: '✅', text: 'BMI正常，继续保持健康的生活方式' })
				}
			}
			
			// 体脂率提示
			if (bodyFat) {
				if (bodyFat > 25) {
					tips.push({ icon: '🏃', text: '体脂率偏高，建议增加有氧运动和力量训练' })
				} else if (bodyFat < 15) {
					tips.push({ icon: '🥗', text: '体脂率偏低，注意补充营养' })
				}
			}
			
			// 内脏脂肪提示
			if (visceralFat) {
				if (visceralFat > 14) {
					tips.push({ icon: '⚠️', text: '内脏脂肪等级高，需要特别注意饮食控制' })
				} else if (visceralFat > 9) {
					tips.push({ icon: '💡', text: '内脏脂肪略高，建议增加运动量' })
				}
			}
			
			if (tips.length === 0) {
				tips.push({ icon: '👍', text: '各项指标正常，继续保持' })
			}
			
			return tips
		}
	},
	onLoad(options) {
		if (options.id) {
			this.bodyTestId = options.id
			this.loadDetail()
		}
	},
	methods: {
		// 加载详情
		async loadDetail() {
			try {
				this.loading = true
				const res = await getBodyTestById(this.bodyTestId)
				this.bodyTestData = res
			} catch (error) {
				console.error('加载详情失败:', error)
				uni.showToast({
					title: '加载失败',
					icon: 'none'
				})
			} finally {
				this.loading = false
			}
		},
		
		// 格式化日期时间
		formatDateTime(dateTime) {
			if (!dateTime) return ''
			const date = new Date(dateTime)
			return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日 ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
		},
		
		// 获取BMI状态
		getBMIStatus(bmi) {
			if (!bmi) return ''
			if (bmi < 18.5) return '偏瘦'
			if (bmi < 24) return '正常'
			if (bmi < 28) return '偏胖'
			if (bmi < 32) return '肥胖'
			return '重度肥胖'
		},
		
		// 获取BMI样式类
		getBMIClass(bmi) {
			if (!bmi) return ''
			if (bmi < 18.5) return 'bmi-low'
			if (bmi < 24) return 'bmi-normal'
			if (bmi < 28) return 'bmi-high'
			return 'bmi-danger'
		},
		
		// 获取内脏脂肪样式类
		getVisceralFatClass(value) {
			if (!value) return ''
			if (value <= 9) return 'normal'
			if (value <= 14) return 'warning'
			return 'danger'
		}
	}
}
</script>

<style scoped>
.page {
	min-height: 100vh;
	background: #f5f5f5;
}

.container {
	padding: 20rpx;
}

.loading-container {
	padding: 200rpx 0;
	text-align: center;
}

.content {
	
}

.time-banner {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	padding: 40rpx 30rpx;
	border-radius: 16rpx;
	margin-bottom: 20rpx;
	text-align: center;
}

.time-text {
	font-size: 32rpx;
	font-weight: bold;
	color: white;
}

.card {
	background: white;
	border-radius: 16rpx;
	padding: 30rpx;
	margin-bottom: 20rpx;
}

.card-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 30rpx;
}

.metrics-grid {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 25rpx;
}

.metric-item {
	background: #f8f8f8;
	border-radius: 12rpx;
	padding: 30rpx 20rpx;
	text-align: center;
}

.metric-label {
	display: block;
	font-size: 24rpx;
	color: #999;
	margin-bottom: 15rpx;
}

.metric-value {
	display: block;
	font-size: 44rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 10rpx;
}

.metric-value.bmi-normal {
	color: #4CAF50;
}

.metric-value.bmi-low {
	color: #FFC107;
}

.metric-value.bmi-high {
	color: #FF9800;
}

.metric-value.bmi-danger {
	color: #F44336;
}

.metric-unit {
	display: block;
	font-size: 22rpx;
	color: #999;
}

.composition-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 20rpx;
}

.composition-item {
	text-align: center;
}

.composition-icon {
	font-size: 50rpx;
	margin-bottom: 15rpx;
}

.composition-label {
	display: block;
	font-size: 24rpx;
	color: #999;
	margin-bottom: 10rpx;
}

.composition-value {
	display: block;
	font-size: 26rpx;
	font-weight: bold;
	color: #333;
}

.composition-value.normal {
	color: #4CAF50;
}

.composition-value.warning {
	color: #FF9800;
}

.composition-value.danger {
	color: #F44336;
}

.health-tips {
	
}

.tip-item {
	display: flex;
	align-items: flex-start;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.tip-item:last-child {
	border-bottom: none;
}

.tip-icon {
	font-size: 36rpx;
	margin-right: 15rpx;
	flex-shrink: 0;
}

.tip-text {
	flex: 1;
	font-size: 26rpx;
	color: #666;
	line-height: 1.6;
}

.remark-text {
	font-size: 28rpx;
	color: #666;
	line-height: 1.6;
}

.tester-name {
	font-size: 28rpx;
	color: #333;
}
</style>
