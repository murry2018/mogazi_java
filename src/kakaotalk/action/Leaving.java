package kakaotalk.action;

import java.time.LocalDateTime;

// 채팅방에서 유저가 스스로 떠나는 행위
// '[유저명]님이 나가셨습니다' 꼴의 패턴으로 나타난다.
public class Leaving extends Action implements ExitAction{
    public Leaving(LocalDateTime time) {
        super(time);
    }
}
