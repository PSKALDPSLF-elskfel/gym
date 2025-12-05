
import {computed,unref} from 'vue'
import {SYSTEM_WINDOW_INFO,MENU_BUTTON_RECT_INFO} from "@/utils/config.js"
// 状态栏高度(px)
export const statusBarH = computed(()=>SYSTEM_WINDOW_INFO.statusBarHeight||25)


//标题栏高度
export const titleBarH = computed(()=>{
	
	 const {top,height} = MENU_BUTTON_RECT_INFO
	 if(!top||!height) return 40
	 return height +(top-unref(statusBarH)) * 2
})

export const navBarH = computed(()=>unref(statusBarH)+unref(titleBarH))
export const useNavBarStyle = () => {
  const statusBarHeight = computed(() => unref(statusBarH) + 'px');
  const titleBarHeight = computed(() => unref(titleBarH) + 'px');
  const navBarHeight = computed(() => unref(navBarH) + 'px');

  return {
    statusBarHeight,
    titleBarHeight,
    navBarHeight
  };
};