<template>
	<view class="page">
		<!-- 自定义导航栏 -->
		<mod-nav-bar title="数据对比" :back="true" />
		
		<view class="container">
			<!-- 加载状态 -->
			<view v-if="loading" class="loading-container">
				<uni-load-more status="loading" />
			</view>
			
			<!-- 内容 -->
			<view v-else class="content">
				<!-- 选择对比记录 -->
				<view class="card">
					<view class="card-title">选择对比记录</view>
					<view class="select-row">
						<view class="select-item">
							<text class="select-label">记录1</text>
							<picker mode="selector" :range="recordList" range-key="displayText" @change="onRecord1Change">
								<view class="select-value">
									{{ record1 ? record1.displayText : '请选择' }}
									<text class="arrow">›</text>
								</view>
							</picker>
						</view>
						<view class="select-item">
							<text class="select-label">记录2</text>
							<picker mode="selector" :range="recordList" range-key="displayText" @change="onRecord2Change">
								<view class="select-value">
									{{ record2 ? record2.displayText : '请选择' }}
									<text class="arrow">›</text>
								</view>
							</picker>
						</view>
					</view>
				</view>
				
				<!-- 对比结果 -->
				<view v-if="record1 && record2" class="compare-result">
					<!-- 基本指标对比 -->
					<view class="card">
						<view class="card-title">基本指标对比</view>
						<view class="compare-table">
							<view class="table-row header">
								<text class="cell">指标</text>
								<text class="cell">{{ formatShortDate(record1.testTime) }}</text>
								<text class="cell">{{ formatShortDate(record2.testTime) }}</text>
								<text class="cell">变化</text>
							</view>
							<view class="table-row">
								<text class="cell label">身高</text>
								<text class="cell">{{ record1.height || '-' }}</text>
								<text class="cell">{{ record2.height || '-' }}</text>
								<text class="cell change" :class="getChangeClass(record2.height - record1.height)">
									{{ formatChange(record2.height - record1.height, 'cm') }}
								</text>
							</view>
							<view class="table-row">
								<text class="cell label">体重</text>
								<text class="cell">{{ record1.weight || '-' }}</text>
								<text class="cell">{{ record2.weight || '-' }}</text>
								<text class="cell change" :class="getChangeClass(record1.weight - record2.weight)">
									{{ formatChange(record2.weight - record1.weight, 'kg') }}
								</text>
							</view>
							<view class="table-row">
								<text class="cell label">BMI</text>
								<text class="cell">{{ record1.bmi ? record1.bmi.toFixed(1) : '-' }}</text>
								<text class="cell">{{ record2.bmi ? record2.bmi.toFixed(1) : '-' }}</text>
								<text class="cell change" :class="getChangeClass(record1.bmi - record2.bmi)">
									{{ formatChange(record2.bmi - record1.bmi, '') }}
								</text>
							</view>
							<view class="table-row">
								<text class="cell label">体脂率</text>
								<text class="cell">{{ record1.bodyFat || '-' }}</text>
								<text class="cell">{{ record2.bodyFat || '-' }}</text>
								<text class="cell change" :class="getChangeClass(record1.bodyFat - record2.bodyFat)">
									{{ formatChange(record2.bodyFat - record1.bodyFat, '%') }}
								</text>
							</view>
							<view class="table-row">
								<text class="cell label">肌肉量</text>
								<text class="cell">{{ record1.muscleMass || '-' }}</text>
								<text class="cell">{{ record2.muscleMass || '-' }}</text>
								<text class="cell change" :class="getChangeClass(record2.muscleMass - record1.muscleMass)">
									{{ formatChange(record2.muscleMass - record1.muscleMass, 'kg') }}
								</text>
							</view>
						</view>
					</view>
					
					<!-- 趋势图表 -->
					<view class="card">
						<view class="card-title">体重趋势</view>
						<view class="chart-container">
							<qiun-data-charts 
								type="line"
								:opts="chartOpts"
								:chartData="chartData"
								:style="{height: chartHeight}"
							/>
						</view>
					</view>
					
					<!-- BMI趋势 -->
					<view class="card">
						<view class="card-title">BMI趋势</view>
						<view class="chart-container">
							<qiun-data-charts 
								type="line"
								:opts="bmiChartOpts"
								:chartData="bmiChartData"
								:style="{height: chartHeight}"
							/>
						</view>
					</view>
					
					<!-- 对比总结 -->
					<view class="card">
						<view class="card-title">对比总结</view>
						<view class="summary-list">
							<view v-for="(item, index) in summaryList" :key="index" class="summary-item">
								<text class="summary-icon">{{ item.icon }}</text>
								<text class="summary-text">{{ item.text }}</text>
							</view>
						</view>
					</view>
				</view>
				
				<!-- 未选择提示 -->
				<view v-else class="empty-tip">
					<text class="tip-text">请选择两条记录进行对比</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { getBodyTestPage } from '@/apis/bodyTest.js'
import { getCurrentUser } from '@/apis/user.js'
import ModNavBar from '@/components/mod-nav-bar/mod-nav-bar.vue'

export default {
	components: {
		ModNavBar
	},
	data() {
		return {
			loading: true,
			userId: null,
			recordList: [],
			record1: null,
			record2: null,
			chartHeight: '400rpx',
			// 体重图表配置
			chartOpts: {
				color: ["#667eea"],
				padding: [15, 15, 0, 5],
				enableScroll: false,
				legend: {
					show: false
				},
				xAxis: {
					disableGrid: true
				},
				yAxis: {
					gridType: "dash",
					dashLength: 2
				},
				extra: {
					line: {
						type: "curve",
						width: 2,
						activeType: "hollow"
					}
				}
			},
			chartData: {},
			// BMI图表配置
			bmiChartOpts: {
				color: ["#764ba2"],
				padding: [15, 15, 0, 5],
				enableScroll: false,
				legend: {
					show: false
				},
				xAxis: {
					disableGrid: true
				},
				yAxis: {
					gridType: "dash",
					dashLength: 2
				},
				extra: {
					line: {
						type: "curve",
						width: 2,
						activeType: "hollow"
					}
				}
			},
			bmiChartData: {}
		}
	},
	computed: {
		// 对比总结
		summaryList() {
			if (!this.record1 || !this.record2) return []
			
			const summary = []
			const weightChange = this.record2.weight - this.record1.weight
			const bmiChange = this.record2.bmi - this.record1.bmi
			const bodyFatChange = this.record2.bodyFat - this.record1.bodyFat
			const muscleMassChange = this.record2.muscleMass - this.record1.muscleMass
			
			// 体重变化
			if (Math.abs(weightChange) > 0.1) {
				if (weightChange > 0) {
					summary.push({ icon: '📈', text: `体重增加了 ${weightChange.toFixed(1)} kg` })
				} else {
					summary.push({ icon: '📉', text: `体重减少了 ${Math.abs(weightChange).toFixed(1)} kg` })
				}
			} else {
				summary.push({ icon: '➡️', text: '体重基本保持稳定' })
			}
			
			// BMI变化
			if (Math.abs(bmiChange) > 0.1) {
				if (bmiChange > 0) {
					summary.push({ icon: '📊', text: `BMI指数上升了 ${bmiChange.toFixed(1)}` })
				} else {
					summary.push({ icon: '📊', text: `BMI指数下降了 ${Math.abs(bmiChange).toFixed(1)}` })
				}
			}
			
			// 体脂率变化
			if (bodyFatChange && Math.abs(bodyFatChange) > 0.1) {
				if (bodyFatChange > 0) {
					summary.push({ icon: '⬆️', text: `体脂率上升了 ${bodyFatChange.toFixed(1)}%` })
				} else {
					summary.push({ icon: '⬇️', text: `体脂率下降了 ${Math.abs(bodyFatChange).toFixed(1)}%` })
				}
			}
			
			// 肌肉量变化
			if (muscleMassChange && Math.abs(muscleMassChange) > 0.1) {
				if (muscleMassChange > 0) {
					summary.push({ icon: '💪', text: `肌肉量增加了 ${muscleMassChange.toFixed(1)} kg` })
				} else {
					summary.push({ icon: '⚠️', text: `肌肉量减少了 ${Math.abs(muscleMassChange).toFixed(1)} kg` })
				}
			}
			
			// 总体评价
			if (weightChange < 0 && bodyFatChange < 0 && muscleMassChange >= 0) {
				summary.push({ icon: '🎉', text: '恭喜！您的身体成分在向好的方向发展' })
			}
			
			return summary
		}
	},
	onLoad() {
		this.loadUserAndData()
	},
	methods: {
		// 加载用户和数据
		async loadUserAndData() {
			try {
				this.loading = true
				// 获取当前用户
				const userRes = await getCurrentUser()
				this.userId = userRes.id
				
				// 加载体测记录
				await this.loadRecords()
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
		
		// 加载记录列表
		async loadRecords() {
			try {
				const res = await getBodyTestPage({
					currentPage: 1,
					pageSize: 50,
					userId: this.userId
				})
				
				this.recordList = (res.records || []).map(item => ({
					...item,
					displayText: this.formatDate(item.testTime)
				}))
			} catch (error) {
				console.error('加载记录失败:', error)
				throw error
			}
		},
		
		// 选择记录1
		onRecord1Change(e) {
			this.record1 = this.recordList[e.detail.value]
			this.updateCharts()
		},
		
		// 选择记录2
		onRecord2Change(e) {
			this.record2 = this.recordList[e.detail.value]
			this.updateCharts()
		},
		
		// 更新图表
		updateCharts() {
			if (!this.record1 || !this.record2) return
			
			// 体重趋势图
			this.chartData = {
				categories: [this.formatShortDate(this.record1.testTime), this.formatShortDate(this.record2.testTime)],
				series: [{
					name: "体重",
					data: [this.record1.weight || 0, this.record2.weight || 0]
				}]
			}
			
			// BMI趋势图
			this.bmiChartData = {
				categories: [this.formatShortDate(this.record1.testTime), this.formatShortDate(this.record2.testTime)],
				series: [{
					name: "BMI",
					data: [this.record1.bmi || 0, this.record2.bmi || 0]
				}]
			}
		},
		
		// 格式化日期
		formatDate(dateTime) {
			if (!dateTime) return ''
			const date = new Date(dateTime)
			return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
		},
		
		// 格式化短日期
		formatShortDate(dateTime) {
			if (!dateTime) return ''
			const date = new Date(dateTime)
			return `${date.getMonth() + 1}/${date.getDate()}`
		},
		
		// 格式化变化值
		formatChange(value, unit) {
			if (!value || isNaN(value)) return '-'
			const absValue = Math.abs(value).toFixed(1)
			const sign = value > 0 ? '+' : '-'
			return `${sign}${absValue}${unit}`
		},
		
		// 获取变化样式类
		getChangeClass(value) {
			if (!value || isNaN(value)) return ''
			if (value > 0.1) return 'positive'
			if (value < -0.1) return 'negative'
			return 'neutral'
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
	padding-bottom: 40rpx;
}

.loading-container {
	padding: 200rpx 0;
	text-align: center;
}

.content {
	
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

.select-row {
	display: flex;
	gap: 20rpx;
}

.select-item {
	flex: 1;
}

.select-label {
	display: block;
	font-size: 24rpx;
	color: #999;
	margin-bottom: 15rpx;
}

.select-value {
	background: #f8f8f8;
	border-radius: 8rpx;
	padding: 20rpx;
	font-size: 26rpx;
	color: #333;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.arrow {
	font-size: 32rpx;
	color: #ccc;
}

.compare-table {
	
}

.table-row {
	display: flex;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.table-row.header {
	background: #f8f8f8;
	border-radius: 8rpx;
	margin-bottom: 10rpx;
	padding: 20rpx 15rpx;
	font-weight: bold;
}

.table-row:last-child {
	border-bottom: none;
}

.cell {
	flex: 1;
	text-align: center;
	font-size: 26rpx;
	color: #666;
}

.cell.label {
	text-align: left;
	color: #333;
	font-weight: 500;
}

.cell.change {
	font-weight: bold;
}

.cell.change.positive {
	color: #4CAF50;
}

.cell.change.negative {
	color: #F44336;
}

.cell.change.neutral {
	color: #999;
}

.chart-container {
	width: 100%;
	margin-top: 20rpx;
}

.summary-list {
	
}

.summary-item {
	display: flex;
	align-items: flex-start;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.summary-item:last-child {
	border-bottom: none;
}

.summary-icon {
	font-size: 36rpx;
	margin-right: 15rpx;
	flex-shrink: 0;
}

.summary-text {
	flex: 1;
	font-size: 26rpx;
	color: #666;
	line-height: 1.6;
}

.empty-tip {
	padding: 200rpx 40rpx;
	text-align: center;
}

.tip-text {
	font-size: 28rpx;
	color: #999;
}
</style>
