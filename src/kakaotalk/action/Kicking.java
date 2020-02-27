package kakaotalk.action;

import java.time.LocalDateTime;

// 채팅방 관리자가 유저를 내보내는 행위
// '[유저명]님을 내보냈습니다' 꼴의 패턴으로 나타난다.
public class Kicking extends Action implements AdminAction {
    final String content;
    Kicking(LocalDateTime time, String who) {
        super(time);
        content = who;
    }
    @Override
    public String getContent() {
        return content;
    }
}
