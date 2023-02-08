package com.jycforest29.commerce.chat;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;

// 스프링에서는 TextWebSocketHandler와 BinaryWebSocketHandler를 사용해
// 클라이언트단에서 오는 응답을 서버에서 처리하는 핸들러를 만들 수 있음.
// BinaryWebSocketHandler는 이미지 등.
@Component
public class SocketTextHandler extends TextWebSocketHandler {
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {

        String payload = message.getPayload();
        MsgDto msgDto = new MsgDto("", "", "", LocalDateTime.now());
        session.sendMessage(new TextMessage(msgDto.getMsg()));
    }

}
