package kakaotalk.action;

import java.time.LocalDateTime;

// 유저가 스스로의 대화를 삭제하는 행위
// 관리자가 대화를 지우는 Hiding과 구분하기 위하여 Canceling이라 명명함
// '삭제된 메시지입니다' 꼴로 나타난다.
public class Canceling extends Action implements AdminAction {
    public Canceling(LocalDateTime time) {
        super(time);
    }
}
