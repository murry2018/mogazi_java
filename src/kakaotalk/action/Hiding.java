package kakaotalk.action;

import java.time.LocalDateTime;

// 오픈 채팅방 관리자가 대화를 가리는 행위
// '채팅방 관리자가 메시지를 가렸습니다' 꼴로 나타난다.
public class Hiding extends Action implements AdminAction {
    public Hiding(LocalDateTime time) {
        super(time);
    }
}
