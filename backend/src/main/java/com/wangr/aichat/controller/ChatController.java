package com.wangr.aichat.controller;

import com.wangr.aichat.model.ChatRoom;
import com.wangr.aichat.service.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Resource
    private ChatService chatService;

    // 发送聊天消息，返回AI回复
    @PostMapping("/send")
    public String send(@RequestParam("roomId") long roomId,
                       @RequestParam("prompt") String userPrompt) {
        return chatService.doChat(roomId, userPrompt);
    }

    // 获取当前所有聊天房间及其消息
    @GetMapping("/rooms")
    public List<ChatRoom> rooms() {
        return chatService.getChatRoomList();
    }
}
