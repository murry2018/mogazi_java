package kakaotalk.action;

import java.time.LocalDateTime;

// 오픈 채팅방 방장이 바뀌는 행위
// '방장이 xxx에서 yyy로 변경되었습니다.' 꼴로 나타남
public class Rebelling extends Action implements AdminAction {
    public Rebelling(LocalDateTime time) {
        super(time);
    }
}
