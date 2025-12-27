<template>
	<view class="page">
		<!-- 自定义导航栏 -->
		<mod-nav-bar title="体测报告" :back="true" />
		
		<view class="container">
			<!-- 加载状态 -->
			<view v-if="loading" class="loading-container">
				<uni-load-more status="loading" />
			</view>
			
			<!-- 无数据 -->
			<view v-else-if="!bodyTestData" class="empty-container">
				<view class="empty-icon">📊</view>
				<text class="empty-text">暂无体测数据</text>
				<text class="empty-hint">点击下方按钮添加或查看历史记录</text>
				<view class="button-group">
					<button class="btn-primary" @click="goToAddBodyTest">添加体测数据</button>
					<button class="btn-secondary" @click="goToHistory">查看历史记录</button>
				</view>
			</view>
			
			<!-- 体测数据 -->
			<view v-else class="content">
				<!-- 基本信息卡片 -->
				<view class="card">
					<view class="card-header">
						<text class="card-title">基本信息</text>
						<text class="test-time">{{ formatDateTime(bodyTestData.testTime) }}</text>
					</view>
					<view class="basic-info">
						<view class="info-row">
							<view class="info-item">
								<text class="info-label">身高</text>
								<text class="info-value">{{ bodyTestData.height || '-' }} <text class="unit">cm</text></text>
							</view>
							<view class="info-item">
								<text class="info-label">体重</text>
								<text class="info-value">{{ bodyTestData.weight || '-' }} <text class="unit">kg</text></text>
							</view>
						</view>
						<view class="info-row">
							<view class="info-item">
								<text class="info-label">BMI</text>
								<text class="info-value" :class="getBMIClass(bodyTestData.bmi)">
									{{ bodyTestData.bmi ? bodyTestData.bmi.toFixed(1) : '-' }}
								</text>
								<text class="bmi-status">{{ getBMIStatus(bodyTestData.bmi) }}</text>
							</view>
							<view class="info-item">
								<text class="info-label">体脂率</text>
								<text class="info-value">{{ bodyTestData.bodyFat || '-' }} <text class="unit">%</text></text>
							</view>
						</view>
					</view>
				</view>
				
				<!-- 身体成分卡片 -->
				<view class="card">
					<view class="card-header">
						<text class="card-title">身体成分</text>
					</view>
					<view class="composition-list">
						<view class="composition-item">
							<view class="composition-left">
								<text class="composition-icon">💪</text>
								<text class="composition-label">肌肉量</text>
							</view>
							<text class="composition-value">{{ bodyTestData.muscleMass || '-' }} kg</text>
						</view>
						<view class="composition-item">
							<view class="composition-left">
								<text class="composition-icon">🫀</text>
								<text class="composition-label">内脏脂肪等级</text>
							</view>
							<text class="composition-value" :class="getVisceralFatClass(bodyTestData.visceralFat)">
								{{ bodyTestData.visceralFat || '-' }}
							</text>
						</view>
						<view class="composition-item">
							<view class="composition-left">
								<text class="composition-icon">🔥</text>
								<text class="composition-label">基础代谢</text>
							</view>
							<text class="composition-value">{{ bodyTestData.basalMetabolism || '-' }} kcal</text>
						</view>
					</view>
				</view>
				
				<!-- BMI图表卡片 -->
				<view class="card">
					<view class="card-header">
						<text class="card-title">BMI指标</text>
					</view>
					<view class="bmi-chart">
						<view class="bmi-scale">
							<view class="bmi-section" style="background: #4CAF50;">偏瘦</view>
							<view class="bmi-section" style="background: #8BC34A;">正常</view>
							<view class="bmi-section" style="background: #FFC107;">偏胖</view>
							<view class="bmi-section" style="background: #FF9800;">肥胖</view>
							<view class="bmi-section" style="background: #F44336;">重度肥胖</view>
						</view>
						<view class="bmi-labels">
							<text>< 18.5</text>
							<text>18.5-24</text>
							<text>24-28</text>
							<text>28-32</text>
							<text>> 32</text>
						</view>
						<view v-if="bodyTestData.bmi" class="bmi-pointer" :style="{ left: getBMIPointerPosition(bodyTestData.bmi) }">
							<view class="pointer-arrow">▼</view>
							<text class="pointer-value">{{ bodyTestData.bmi.toFixed(1) }}</text>
						</view>
					</view>
				</view>
				
				<!-- 备注 -->
				<view v-if="bodyTestData.remark" class="card">
					<view class="card-header">
						<text class="card-title">备注</text>
					</view>
					<text class="remark-text">{{ bodyTestData.remark }}</text>
				</view>
				
				<!-- 测试人员 -->
				<view v-if="bodyTestData.testerName" class="tester-info">
					<text class="tester-label">测试人员：</text>
					<text class="tester-name">{{ bodyTestData.testerName }}</text>
				</view>
			</view>
		</view>
		
		<!-- 底部按钮 -->
		<view class="footer-actions">
			<button class="btn-secondary" @click="goToAddBodyTest">添加体测</button>
			<button class="btn-secondary" @click="goToHistory">历史记录</button>
			<button class="btn-primary" @click="goToCompare">数据对比</button>
		</view>
	</view>
</template>

<script>
import { getLatestBodyTest } from '@/apis/bodyTest.js'
import { getCurrentUser } from '@/apis/user.js'
import ModNavBar from '@/components/mod-nav-bar/mod-nav-bar.vue'

export default {
	components: {
		ModNavBar
	},
	data() {
		return {
			loading: true,
			bodyTestData: null,
			userId: null
		}
	},
	onLoad() {
		this.loadUserAndData()
	},
	methods: {
		// 加载用户信息和体测数据
		async loadUserAndData() {
			try {
				this.loading = true
				// 获取当前用户
				const userRes = await getCurrentUser()
				this.userId = userRes.id
				
				// 获取最新体测数据
				await this.loadLatestBodyTest()
			} catch (error) {
				console.error('加载数据失败:', error)
			} finally {
				this.loading = false
			}
		},
		
		// 加载最新体测数据
		async loadLatestBodyTest() {
			try {
				const res = await getLatestBodyTest(this.userId, {
					showDefaultMsg: false  // 不显示默认错误提示
				})
				this.bodyTestData = res
			} catch (error) {
				// 如果是"暂无体测数据"，这是正常情况，不需要提示
				if (error.message && error.message.includes('暂无体测数据')) {
					this.bodyTestData = null
				} else {
					// 其他错误才提示
					console.error('获取体测数据失败:', error)
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					})
				}
			}
		},
		
		// 格式化日期时间
		formatDateTime(dateTime) {
			if (!dateTime) return ''
			const date = new Date(dateTime)
			return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
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
		},
		
		// 获取BMI指针位置
		getBMIPointerPosition(bmi) {
			if (!bmi) return '0%'
			let percentage = 0
			if (bmi < 18.5) {
				percentage = (bmi / 18.5) * 20
			} else if (bmi < 24) {
				percentage = 20 + ((bmi - 18.5) / 5.5) * 20
			} else if (bmi < 28) {
				percentage = 40 + ((bmi - 24) / 4) * 20
			} else if (bmi < 32) {
				percentage = 60 + ((bmi - 28) / 4) * 20
			} else {
				percentage = Math.min(95, 80 + ((bmi - 32) / 8) * 20)
			}
			return percentage + '%'
		},
		
		// 跳转到历史记录
		goToHistory() {
			uni.navigateTo({
				url: '/pages/body-test/history'
			})
		},
		
		// 跳转到数据对比
		goToCompare() {
			uni.navigateTo({
				url: '/pages/body-test/compare'
			})
		},
		
		// 跳转到添加体测
		goToAddBodyTest() {
			uni.navigateTo({
				url: '/pages/body-test/add'
			})
		}
	}
}
</script>

<style scoped>
.page {
	min-height: 100vh;
	background: #f5f5f5;
	padding-bottom: 140rpx;
}

.container {
	padding: 20rpx;
}

.loading-container {
	padding: 200rpx 0;
	text-align: center;
}

.empty-container {
	padding: 200rpx 40rpx;
	text-align: center;
}

.empty-icon {
	font-size: 120rpx;
	margin-bottom: 40rpx;
}

.empty-text {
	display: block;
	font-size: 32rpx;
	color: #333;
	font-weight: bold;
	margin-bottom: 20rpx;
}

.empty-hint {
	display: block;
	font-size: 26rpx;
	color: #999;
	margin-bottom: 40rpx;
}

.content {
	padding-bottom: 40rpx;
}

.card {
	background: white;
	border-radius: 16rpx;
	padding: 30rpx;
	margin-bottom: 20rpx;
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30rpx;
}

.card-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.test-time {
	font-size: 24rpx;
	color: #999;
}

.basic-info {
	
}

.info-row {
	display: flex;
	gap: 30rpx;
	margin-bottom: 30rpx;
}

.info-row:last-child {
	margin-bottom: 0;
}

.info-item {
	flex: 1;
	background: #f8f8f8;
	border-radius: 12rpx;
	padding: 30rpx 20rpx;
	text-align: center;
}

.info-label {
	display: block;
	font-size: 24rpx;
	color: #999;
	margin-bottom: 10rpx;
}

.info-value {
	display: block;
	font-size: 40rpx;
	font-weight: bold;
	color: #333;
}

.info-value .unit {
	font-size: 24rpx;
	color: #999;
	font-weight: normal;
	margin-left: 5rpx;
}

.bmi-normal {
	color: #4CAF50;
}

.bmi-low {
	color: #FFC107;
}

.bmi-high {
	color: #FF9800;
}

.bmi-danger {
	color: #F44336;
}

.bmi-status {
	display: block;
	font-size: 24rpx;
	color: #666;
	margin-top: 10rpx;
}

.composition-list {
	
}

.composition-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 25rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.composition-item:last-child {
	border-bottom: none;
}

.composition-left {
	display: flex;
	align-items: center;
}

.composition-icon {
	font-size: 36rpx;
	margin-right: 20rpx;
}

.composition-label {
	font-size: 28rpx;
	color: #333;
}

.composition-value {
	font-size: 30rpx;
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

.bmi-chart {
	position: relative;
	padding: 30rpx 0;
}

.bmi-scale {
	display: flex;
	height: 40rpx;
	border-radius: 20rpx;
	overflow: hidden;
}

.bmi-section {
	flex: 1;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 20rpx;
	color: white;
}

.bmi-labels {
	display: flex;
	justify-content: space-between;
	margin-top: 15rpx;
	padding: 0 10rpx;
}

.bmi-labels text {
	font-size: 20rpx;
	color: #999;
}

.bmi-pointer {
	position: absolute;
	top: 0;
	transform: translateX(-50%);
	display: flex;
	flex-direction: column;
	align-items: center;
}

.pointer-arrow {
	font-size: 28rpx;
	color: #333;
	line-height: 1;
}

.pointer-value {
	font-size: 24rpx;
	font-weight: bold;
	color: #333;
	margin-top: 5rpx;
}

.remark-text {
	font-size: 28rpx;
	color: #666;
	line-height: 1.6;
}

.tester-info {
	padding: 20rpx;
	text-align: center;
}

.tester-label {
	font-size: 24rpx;
	color: #999;
}

.tester-name {
	font-size: 24rpx;
	color: #666;
}

.button-group {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
	margin-top: 40rpx;
}

.button-group .btn-primary,
.button-group .btn-secondary {
	height: 80rpx;
	border-radius: 40rpx;
	font-size: 28rpx;
	border: none;
}

.button-group .btn-primary {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: white;
}

.button-group .btn-secondary {
	background: white;
	color: #667eea;
	border: 2rpx solid #667eea;
}

.footer-actions {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background: white;
	padding: 20rpx;
	display: flex;
	gap: 20rpx;
	box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.btn-primary,
.btn-secondary {
	flex: 1;
	height: 80rpx;
	border-radius: 40rpx;
	font-size: 28rpx;
	border: none;
}

.btn-primary {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: white;
}

.btn-secondary {
	background: white;
	color: #667eea;
	border: 2rpx solid #667eea;
}
</style>
