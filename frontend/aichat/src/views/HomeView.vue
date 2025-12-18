<script setup lang="ts">
import { useRouter } from 'vue-router'
import { BulbOutlined, ArrowRightOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const startGame = () => {
  router.push('/chat')
}
</script>

<template>
  <div class="home-container">
    <div class="content-wrapper">
      <div class="icon-wrapper">
        <BulbOutlined />
      </div>
      <h1 class="title">AI 脑筋急转弯</h1>
      <p class="subtitle">挑战你的思维极限，与 AI 一决高下</p>
      
      <div class="action-area">
        <a-button type="primary" size="large" shape="round" class="start-btn" @click="startGame">
          立即开始
          <ArrowRightOutlined />
        </a-button>
      </div>
    </div>
    
    <div class="footer">
      Powered by AI Chat
    </div>
  </div>
</template>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  position: relative;
  overflow: hidden;
}

/* Decorative background circles */
.home-container::before,
.home-container::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.4);
  z-index: 0;
}

.home-container::before {
  width: 300px;
  height: 300px;
  top: -50px;
  left: -50px;
}

.home-container::after {
  width: 400px;
  height: 400px;
  bottom: -100px;
  right: -100px;
}

.content-wrapper {
  z-index: 1;
  text-align: center;
  padding: 60px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.18);
  max-width: 500px;
  width: 90%;
  transform: translateY(0);
  transition: transform 0.3s ease;
}

.content-wrapper:hover {
  transform: translateY(-5px);
}

.icon-wrapper {
  font-size: 64px;
  color: #faad14;
  margin-bottom: 24px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
  100% { transform: translateY(0px); }
}

.title {
  margin-bottom: 16px;
  font-size: 2.5rem;
  font-weight: 800;
  color: #1f1f1f;
  letter-spacing: -0.5px;
  background: linear-gradient(45deg, #1890ff, #722ed1);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.subtitle {
  font-size: 1.1rem;
  color: #666;
  margin-bottom: 40px;
  line-height: 1.6;
}

.start-btn {
  height: 50px;
  padding: 0 40px;
  font-size: 18px;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(24, 144, 255, 0.3);
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 auto;
  transition: all 0.3s ease;
}

.start-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(24, 144, 255, 0.4);
}

.footer {
  position: absolute;
  bottom: 20px;
  color: #888;
  font-size: 12px;
}
</style>
