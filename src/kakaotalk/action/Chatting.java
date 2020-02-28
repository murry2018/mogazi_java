package kakaotalk.action;

import java.time.LocalDateTime;

// 다른 사용자와 대화하는 행위
// '[유저명] : [대화명]' 꼴의 패턴으로 나타난다.
public class Chatting extends Action implements ActiveAction, ChatAction{
    String content;  // 대화 내용
    public Chatting(LocalDateTime time, String content) {
        super(time);
        this.content = content;
    }
    @Override
    public String getContent() {
        return content;
    }
    public void appendToContent(String content) {
        this.content += content;
    }
}
