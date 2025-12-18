package com.wangr.aichat.service;

import com.wangr.aichat.model.ChatRoom;

import java.util.List;

public interface ChatService {
    // 房间号
    String doChat(long roomId, String userPrompt);
    // 获取当前所有聊天房间对话
    List<ChatRoom> getChatRoomList();
}
