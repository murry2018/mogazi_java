package kakaotalk.action;

import java.time.LocalDateTime;

// 채팅방에서 누군가에게 초대된 행위
// '[다른유저]님이 [유저명]님을 초대했습니다' 꼴의 패턴으로 나타난다.
public class Invited extends Action implements EnterAction {
    final String content;
    public Invited(LocalDateTime time, String invitor) {
        super(time);
        content = invitor;
    }
    @Override
    public String getContent() {
        return content;
    }
}
