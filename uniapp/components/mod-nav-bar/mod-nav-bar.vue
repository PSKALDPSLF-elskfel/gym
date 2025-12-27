<template>
	<view class="mod-nav-bar">
		<view class="fixed-wrap">
			<view class="status-bar"></view>
		<view class="title-bar">
			<view class="arrow-wrap" v-if="showBackArrow" @click.stop="navBack">
				<text class="fa fa-chevron-left icon"></text>
			</view>
			<view class="text-wrap">{{title}}</view>
			<view class="menu-wrap"></view>
		</view>
			</view>
		<view class="block-wrap">
			
	
		</view>
			
	</view>
</template>

<script setup>
import { computed } from 'vue'
import {COLOR_THEME_PRIMARY,MENU_BUTTON_RECT_INFO} from '@/utils/config.js'
import {useNavBarStyle} from '@/utils/system.js'

const {statusBarHeight,titleBarHeight,navBarHeight} = useNavBarStyle()
console.log(MENU_BUTTON_RECT_INFO)
const props = defineProps({
	title:{
		type:String,
		default:""
	},
	titleColor:{
		type:String,
		default:'#000'
	},
	showBack:{
		type:Boolean,
		default:true
	}
})

// 如果没有明确设置showBack，则根据页面栈判断
const showBackArrow = props.showBack && getCurrentPages().length > 1
const titleTextAlign = computed(()=>{
	if(!showBackArrow) return "left"
	return "center"
})
const navBack = ()=>{
	uni.navigateBack({
		fail:()=>{
			uni.reLaunch({
				url:"/pages/index/index"
			})
		}
	})
}

</script>

<style lang="scss" scoped>
.mod-nav-bar{
	width: 750rpx;
	// height: v-bind(navBarHeight);
	.fixed-wrap{
		position: fixed;
		left: 0;
		top: 0;
		width: 100%;
		background-color:v-bind(COLOR_THEME_PRIMARY) !important;
		z-index: 999;
		.status-bar{
			width:100%;
			height: v-bind(statusBarHeight);
		}
		.title-bar{
			display: flex;
			width: 100%;
			text-align: center;
			justify-content: space-between;
			align-items: center;
			height: v-bind(titleBarHeight);
			color:v-bind(titleColor);
		    padding: 0 32rpx;
		    font-size: 32rpx;
		.arrow-wrap{
			height: 100%;
			width: 80rpx;
			flex-shrink: 0;
			display: flex;
			align-items: center;
			
			.icon {
				font-size: 36rpx;
				color: v-bind(titleColor);
			}
		}
			.text-wrap {
				flex: 1;
				color:v-bind(titleColor);
				text-align:v-bind(titleTextAlign);
				font-weight: bold;
				
			}
			.menu-wrap{
				height: 100%;
				width: 80rpx;
				flex-shrink: 0;
			}
		}
	}
.block-wrap {
  width: 100%;
  height: v-bind(navBarHeight); // 关键：占位高度
}
}
</style>