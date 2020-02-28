package kakaotalk.action;

import java.time.LocalDateTime;

// 오픈 채팅방 이용자가 포스트를 공유하는 행위
// '[유저명]님이 포스트를 공유했습니다' 꼴의 형식으로 나타난다.
public class Posting extends Action implements ActiveAction, ChatAction {
    public Posting (LocalDateTime time) {
        super(time);
    }
}
