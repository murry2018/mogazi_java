package kakaotalk.action;

import java.time.LocalDateTime;

// Action: 카카오톡 유저의 행위
// 이 객체를 action 패키지 바깥에서 직접 생성하는 일은 없다.
public class Action {
    final LocalDateTime time;     // 대화 시각
    Action(LocalDateTime time) {
        this.time = time;
    }
    public LocalDateTime getTime() {
        return time;
    }
    public String getContent() {
        return null;
    }
}
