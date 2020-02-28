package kakaotalk.action;

import java.time.LocalDateTime;

// 채팅방에서 유저가 쫓겨난 행위
// '[유저명]님을 내보냈습니다'로 나타난다.
public class Kicked extends Action implements ExitAction {
    public Kicked(LocalDateTime time) {
        super(time);
    }
}
