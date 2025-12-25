<template>
	<view class="page">
		<!-- 自定义导航栏 -->
		<mod-nav-bar title="体测历史" :back="true" />
		
		<view class="container">
			<!-- 加载状态 -->
			<view v-if="loading && historyList.length === 0" class="loading-container">
				<uni-load-more status="loading" />
			</view>
			
			<!-- 空数据 -->
			<view v-else-if="historyList.length === 0" class="empty-container">
				<view class="empty-icon">📋</view>
				<text class="empty-text">暂无体测历史记录</text>
				<text class="empty-hint">您还没有任何体测记录</text>
			</view>
			
			<!-- 历史记录列表 -->
			<view v-else class="history-list">
				<view 
					v-for="item in historyList" 
					:key="item.id" 
					class="history-item"
					@click="viewDetail(item)"
				>
					<view class="item-header">
						<text class="test-date">{{ formatDate(item.testTime) }}</text>
						<view class="bmi-badge" :class="getBMIClass(item.bmi)">
							<text>{{ getBMIStatus(item.bmi) }}</text>
						</view>
					</view>
					
					<view class="item-body">
						<view class="data-row">
							<view class="data-item">
								<text class="data-label">身高</text>
								<text class="data-value">{{ item.height || '-' }} cm</text>
							</view>
							<view class="data-item">
								<text class="data-label">体重</text>
								<text class="data-value">{{ item.weight || '-' }} kg</text>
							</view>
							<view class="data-item">
								<text class="data-label">BMI</text>
								<text class="data-value" :class="getBMIClass(item.bmi)">
									{{ item.bmi ? item.bmi.toFixed(1) : '-' }}
								</text>
							</view>
							<view class="data-item">
								<text class="data-label">体脂率</text>
								<text class="data-value">{{ item.bodyFat || '-' }}%</text>
							</view>
						</view>
					</view>
					
					<view class="item-footer">
						<text class="tester-info" v-if="item.testerName">测试人员：{{ item.testerName }}</text>
						<text class="arrow-icon">›</text>
					</view>
				</view>
			</view>
			
			<!-- 加载更多 -->
			<view v-if="historyList.length > 0" class="load-more">
				<uni-load-more :status="loadMoreStatus" />
			</view>
		</view>
	</view>
</template>

<script>
import { getBodyTestPage } from '@/apis/bodyTest.js'
import { getCurrentUser } from '@/apis/user.js'
import ModNavBar from '@/components/mod-nav-bar/mod-nav-bar.vue'
import DateUtils from '@/utils/dateUtils.js'

export default {
	components: {
		ModNavBar
	},
	data() {
		return {
			loading: true,
			historyList: [],
			userId: null,
			// 分页参数
			currentPage: 1,
			pageSize: 10,
			total: 0,
			loadMoreStatus: 'more' // more loading noMore
		}
	},
	onLoad() {
		this.loadUserAndData()
	},
	onReachBottom() {
		if (this.loadMoreStatus === 'more') {
			this.loadMore()
		}
	},
	methods: {
		// 加载用户信息和数据
		async loadUserAndData() {
			try {
				this.loading = true
				// 获取当前用户
				const userRes = await getCurrentUser()
				this.userId = userRes.id
				
				// 加载历史记录
				await this.loadHistory()
			} catch (error) {
				console.error('加载数据失败:', error)
				uni.showToast({
					title: '加载失败',
					icon: 'none'
				})
			} finally {
				this.loading = false
			}
		},
		
		// 加载历史记录
		async loadHistory() {
			try {
				const res = await getBodyTestPage({
					currentPage: this.currentPage,
					pageSize: this.pageSize,
					userId: this.userId
				})
				
				this.historyList = res.records || []
				this.total = res.total || 0
				
				// 更新加载更多状态
				if (this.historyList.length >= this.total) {
					this.loadMoreStatus = 'noMore'
				} else {
					this.loadMoreStatus = 'more'
				}
			} catch (error) {
				console.error('加载历史记录失败:', error)
				throw error
			}
		},
		
		// 加载更多
		async loadMore() {
			if (this.historyList.length >= this.total) {
				this.loadMoreStatus = 'noMore'
				return
			}
			
			try {
				this.loadMoreStatus = 'loading'
				this.currentPage++
				
				const res = await getBodyTestPage({
					currentPage: this.currentPage,
					pageSize: this.pageSize,
					userId: this.userId
				})
				
				this.historyList = [...this.historyList, ...(res.records || [])]
				this.total = res.total || 0
				
				// 更新加载更多状态
				if (this.historyList.length >= this.total) {
					this.loadMoreStatus = 'noMore'
				} else {
					this.loadMoreStatus = 'more'
				}
			} catch (error) {
				console.error('加载更多失败:', error)
				this.currentPage--
				this.loadMoreStatus = 'more'
				uni.showToast({
					title: '加载失败',
					icon: 'none'
				})
			}
		},
		
		// 查看详情
		viewDetail(item) {
			uni.navigateTo({
				url: `/pages/body-test/detail?id=${item.id}`
			})
		},
		
		// 格式化日期
		formatDate(dateTime) {
			return DateUtils.formatDate(dateTime)
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
}

.history-list {
	
}

.history-item {
	background: white;
	border-radius: 16rpx;
	padding: 30rpx;
	margin-bottom: 20rpx;
}

.item-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 25rpx;
}

.test-date {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
}

.bmi-badge {
	padding: 8rpx 20rpx;
	border-radius: 20rpx;
	font-size: 22rpx;
	color: white;
}

.bmi-badge.bmi-normal {
	background: #4CAF50;
}

.bmi-badge.bmi-low {
	background: #FFC107;
}

.bmi-badge.bmi-high {
	background: #FF9800;
}

.bmi-badge.bmi-danger {
	background: #F44336;
}

.item-body {
	margin-bottom: 20rpx;
}

.data-row {
	display: flex;
	justify-content: space-between;
}

.data-item {
	flex: 1;
	text-align: center;
}

.data-label {
	display: block;
	font-size: 24rpx;
	color: #999;
	margin-bottom: 10rpx;
}

.data-value {
	display: block;
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
}

.data-value.bmi-normal {
	color: #4CAF50;
}

.data-value.bmi-low {
	color: #FFC107;
}

.data-value.bmi-high {
	color: #FF9800;
}

.data-value.bmi-danger {
	color: #F44336;
}

.item-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding-top: 20rpx;
	border-top: 1rpx solid #f0f0f0;
}

.tester-info {
	font-size: 24rpx;
	color: #999;
}

.arrow-icon {
	font-size: 40rpx;
	color: #ccc;
}

.load-more {
	padding: 30rpx 0;
}
</style>
