package kakaotalk.action;

import java.time.LocalDateTime;

// 오픈 채팅방에서, 스스로 입장하는 행위
// '[유저명]님이 입장하셨습니다' 꼴의 패턴으로 나타난다.
public class Joining extends Action implements ActiveAction, EnterAction {
    public Joining(LocalDateTime time) {
        super(time);
    }
}
