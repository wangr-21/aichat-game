<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'
import { UserOutlined, RobotOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'

// Types
interface Message {
  id: number
  content: string
  isUser: boolean
}

interface ChatRoom {
  roomId: number
  // Add other properties if needed based on backend response
}

// State
const roomId = ref<number>(0)
const inputMessage = ref('')
const messages = ref<Message[]>([])
const isGameStarted = ref(false)
const isGameEnded = ref(false)
const isLoading = ref(false)
const chatContainer = ref<HTMLElement | null>(null)
const roomList = ref<ChatRoom[]>([])

// Backend URL configuration
const API_BASE_URL = 'http://localhost:8080/api/chat'

// Generate random room ID
const generateRoomId = () => {
  return Math.floor(Math.random() * 1000000) + 1
}

// Scroll to bottom of chat
const scrollToBottom = async () => {
  await nextTick()
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

// Fetch room list (History)
const fetchRoomList = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/rooms`)
    roomList.value = response.data
  } catch (error) {
    console.error('Failed to fetch room list:', error)
    message.error('无法连接到服务器，请确认后端服务已在 8080 端口启动')
  }
}

// Send message to backend
const sendMessageToBackend = async (prompt: string) => {
  try {
    isLoading.value = true
    const response = await axios.post(`${API_BASE_URL}/send`, null, {
      params: {
        roomId: roomId.value,
        prompt: prompt
      }
    })
    return response.data
  } catch (error) {
    console.error('Chat error:', error)
    message.error('发送消息失败，请重试')
    return null
  } finally {
    isLoading.value = false
  }
}

// Start Game
const startGame = async () => {
  if (isGameStarted.value) return
  
  // Add user "Start" message locally? Or just system message?
  // Requirement: "Click start button or input 'Start'... call backend... send 'Start'"
  // Requirement: "Show my input... and AI reply"
  
  // Let's treat "Start" as a user message
  messages.value.push({
    id: Date.now(),
    content: '开始',
    isUser: true
  })
  
  const reply = await sendMessageToBackend('开始')
  
  if (reply) {
    isGameStarted.value = true
    messages.value.push({
      id: Date.now() + 1,
      content: reply,
      isUser: false
    })
    checkGameOver(reply)
    scrollToBottom()
    fetchRoomList() // Refresh history
  }
}

// End Game (Active)
const endGame = () => {
  // User actively ends game. 
  // Maybe send "结束" to backend? Or just mark locally?
  // Requirement: "User can actively end"
  // Let's send "结束" to backend to see if AI has a specific response, or just close it.
  handleSendMessage('结束')
}

// Handle Send Message
const handleSendMessage = async (msg: string = inputMessage.value) => {
  if (!msg.trim() || isLoading.value || isGameEnded.value) return

  // Add user message
  messages.value.push({
    id: Date.now(),
    content: msg,
    isUser: true
  })
  
  inputMessage.value = ''
  scrollToBottom()

  // If not started and user types "开始", treat as start
  if (!isGameStarted.value && msg.trim() === '开始') {
    isGameStarted.value = true
  }

  const reply = await sendMessageToBackend(msg)
  
  if (reply) {
    messages.value.push({
      id: Date.now() + 1,
      content: reply,
      isUser: false
    })
    checkGameOver(reply)
    scrollToBottom()
  }
}

// Check for Game Over
const checkGameOver = (reply: string) => {
  if (reply.includes('【游戏已结束】') || reply.includes('游戏已结束')) {
    isGameEnded.value = true
  }
}

// Initialize
onMounted(() => {
  roomId.value = generateRoomId()
  fetchRoomList()
})

const loadRoom = (room: ChatRoom) => {
  roomId.value = room.roomId
  messages.value = []
  isGameStarted.value = false
  isGameEnded.value = false
  message.info(`已切换到房间 ${room.roomId}`)
}

</script>

<template>
  <div class="chat-layout">
    <!-- Left Sidebar: History -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h3>历史对话</h3>
      </div>
      <div class="room-list">
        <div v-for="room in roomList" :key="room.roomId" class="room-item" @click="loadRoom(room)">
          <div class="room-icon">
            <RobotOutlined />
          </div>
          <div class="room-info">
            <span class="room-id">房间 #{{ room.roomId }}</span>
            <span class="room-desc">点击查看详情</span>
          </div>
        </div>
        <div v-if="roomList.length === 0" class="no-history">
          <div class="empty-state">
            <RobotOutlined style="font-size: 24px; color: #ccc; margin-bottom: 8px;" />
            <span>暂无历史记录</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Right: Chat Area -->
    <div class="chat-main">
      <div class="chat-header">
        <div class="header-content">
          <h2>房间号: {{ roomId }}</h2>
          <span class="status-badge" :class="{ 'active': isGameStarted && !isGameEnded, 'ended': isGameEnded }">
            {{ isGameEnded ? '已结束' : (isGameStarted ? '进行中' : '未开始') }}
          </span>
        </div>
      </div>

      <div class="messages-container" ref="chatContainer">
        <div v-if="messages.length === 0" class="welcome-placeholder">
          <RobotOutlined style="font-size: 48px; color: #d9d9d9; margin-bottom: 16px;" />
          <p>点击下方“开始”按钮，开启你的脑筋急转弯之旅！</p>
        </div>
        
        <div v-for="msg in messages" :key="msg.id" 
             class="message-wrapper" 
             :class="{ 'user-message': msg.isUser, 'ai-message': !msg.isUser }">
          
          <div class="avatar" v-if="!msg.isUser">
            <a-avatar size="large" style="background-color: #722ed1">
              <template #icon><RobotOutlined /></template>
            </a-avatar>
          </div>

          <div class="message-bubble">
            {{ msg.content }}
          </div>

          <div class="avatar" v-if="msg.isUser">
            <a-avatar size="large" style="background-color: #1890ff">
              <template #icon><UserOutlined /></template>
            </a-avatar>
          </div>
        </div>
      </div>

      <div class="input-area">
        <div class="control-buttons">
          <a-button type="primary" shape="round" :disabled="isGameStarted" @click="startGame">
            <template #icon><RobotOutlined /></template>
            开始游戏
          </a-button>
          <a-button danger shape="round" :disabled="isGameEnded || !isGameStarted" @click="endGame" style="margin-left: 10px">
            结束游戏
          </a-button>
        </div>
        <div class="input-box-wrapper">
          <a-textarea
            v-model:value="inputMessage"
            placeholder="输入你的回答..."
            :auto-size="{ minRows: 1, maxRows: 4 }"
            @pressEnter.prevent="handleSendMessage()"
            :disabled="isGameEnded"
            class="custom-textarea"
          />
          <a-button type="primary" shape="circle" size="large" class="send-btn" @click="handleSendMessage()" :loading="isLoading" :disabled="isGameEnded">
            <template #icon><UserOutlined style="transform: rotate(-45deg); margin-left: 2px;" /></template>
          </a-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chat-layout {
  display: flex;
  height: 100vh;
  width: 100%;
  background-color: #f5f7fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* Sidebar Styles */
.sidebar {
  width: 280px;
  background-color: #ffffff;
  border-right: 1px solid #eef0f5;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0,0,0,0.02);
  z-index: 10;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.sidebar-header h3 {
  margin: 0;
  color: #1f1f1f;
  font-weight: 600;
}

.room-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.room-item {
  display: flex;
  align-items: center;
  padding: 12px;
  margin-bottom: 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.room-item:hover {
  background-color: #f5f7fa;
  border-color: #e6f7ff;
}

.room-icon {
  width: 36px;
  height: 36px;
  background-color: #f0f5ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1890ff;
  margin-right: 12px;
}

.room-info {
  display: flex;
  flex-direction: column;
}

.room-id {
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.room-desc {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.no-history {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #999;
  font-size: 14px;
}

/* Main Chat Area Styles */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.chat-header {
  padding: 16px 24px;
  background-color: #ffffff;
  border-bottom: 1px solid #eef0f5;
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.chat-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f1f1f;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  background-color: #f0f0f0;
  color: #999;
}

.status-badge.active {
  background-color: #e6f7ff;
  color: #1890ff;
}

.status-badge.ended {
  background-color: #fff1f0;
  color: #ff4d4f;
}

.messages-container {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.welcome-placeholder {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  opacity: 0.7;
}

.message-wrapper {
  display: flex;
  align-items: flex-start;
  max-width: 75%;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.user-message {
  align-self: flex-end;
  flex-direction: row;
}

.ai-message {
  align-self: flex-start;
  flex-direction: row;
}

.avatar {
  margin: 0 12px;
  flex-shrink: 0;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 15px;
  line-height: 1.6;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  white-space: pre-wrap;
  word-break: break-word;
  position: relative;
}

.ai-message .message-bubble {
  background-color: #ffffff;
  color: #333;
  border-top-left-radius: 2px;
}

.user-message .message-bubble {
  background-color: #1890ff;
  color: #ffffff;
  border-top-right-radius: 2px;
}

/* Input Area Styles */
.input-area {
  padding: 24px;
  background-color: #ffffff;
  border-top: 1px solid #eef0f5;
}

.control-buttons {
  margin-bottom: 16px;
  display: flex;
  justify-content: center;
}

.input-box-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  background-color: #f5f7fa;
  padding: 8px;
  border-radius: 24px;
  border: 1px solid transparent;
  transition: all 0.3s;
}

.input-box-wrapper:focus-within {
  background-color: #fff;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
}

.custom-textarea {
  border: none !important;
  background: transparent !important;
  box-shadow: none !important;
  resize: none;
  padding: 8px 12px;
}

.custom-textarea:focus {
  box-shadow: none;
}

.send-btn {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
