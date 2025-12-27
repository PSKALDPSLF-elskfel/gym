import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    uni({
      vueOptions: {
        // Vue 3 特定配置
      }
    }),
  ],
  define: {
    // 确保微信小程序环境下的全局变量正确定义
    'process.env': {
      NODE_ENV: JSON.stringify(process.env.NODE_ENV || 'development')
    }
  },
  server: {
    port: 5173,
    host: '0.0.0.0',
    // 配置代理解决跨域问题
    proxy: {
      // 只代理真正的 API 请求，而不是所有以 /api 开头的路径
      // 使用更精确的匹配规则，避免拦截前端资源文件
      '/api/': {
        target: 'http://localhost:8888', // 后端API地址
        changeOrigin: true,
        secure: false,
        ws: true, // 支持websocket
        // 不需要重写路径，因为后端也是 /api 开头
        configure: (proxy, options) => {
          proxy.on('error', (err, _req, _res) => {
            console.log('proxy error', err);
          });
          proxy.on('proxyReq', (proxyReq, req, _res) => {
            console.log('Sending Request to the Target:', req.method, req.url);
          });
          proxy.on('proxyRes', (proxyRes, req, _res) => {
            console.log('Received Response from the Target:', proxyRes.statusCode, req.url);
          });
        },
      },
      '/files': {
        target: 'http://localhost:8888',
        changeOrigin: true,
      }
      
    }
  },
  // 优化配置
  optimizeDeps: {
    exclude: ['vue-demi'],
    // 移除 include，让 Vite 自动检测依赖
    // Uniapp 使用自定义的 Vue 构建版本
  },
  build: {
    // 微信小程序特定构建配置
    target: 'es6',
    minify: 'terser',
    cssCodeSplit: true,
    // 确保正确处理动态导入
    rollupOptions: {
      output: {
        manualChunks: undefined,
        // 确保导出格式正确
        format: 'es'
      }
    },
    // 提高兼容性
    commonjsOptions: {
      transformMixedEsModules: true
    }
  },

})

