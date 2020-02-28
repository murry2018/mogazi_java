package kakaotalk.action;

import java.time.LocalDateTime;

// 일반 채팅방에서 누군가를 초대하는 행위
// '[유저명]님이 [다른유저]님을 초대했습니다' 꼴의 패턴으로 나타난다.
public class Inviting extends Action implements ActiveAction {
    final String content;
    public Inviting(LocalDateTime time, String who) {
        super(time);
        content = who;
    }
    @Override
    public String getContent() {
        return content;
    }
}
