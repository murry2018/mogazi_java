package kakaotalk;

import kakaotalk.action.Action;
import kakaotalk.action.ActiveAction;
import kakaotalk.action.Kicked;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;

public class Member {
    String name;             // 이용자명
    LocalDateTime lastEnter; // 마지막 입장 시각
    boolean exist;           // 채팅방에 들어와있는가?
    int nAction = 0;         // 주체적 행동의 횟수(대화수)
    int nKicked = 0;         // 쫓겨난 횟수
    final ArrayList<Action> actions;  // 행위 목록
    Member(String name, boolean exist, LocalDateTime lastEnter) {
        this.name = name;
        this.exist = exist;
        this.lastEnter = lastEnter;
        actions = new ArrayList<>();
    }
    public String getName() {
        return name;
    }
    public boolean isExist() {
        return exist;
    }
    void setExist(boolean exist) {
        this.exist = exist;
    }
    public int numOfAction() {
        return nAction;
    }
    public int numOfKicked() {
        return nKicked;
    }
    public LocalDateTime getLastEnter() {
        return lastEnter;
    }
    public void setLastEnter(LocalDateTime lastEnter) {
        this.lastEnter = lastEnter;
    }
    public ArrayList<Action> getActions() {
        return actions;
    }
    public void insertAction(Action action) {
        if (action instanceof ActiveAction)
            nAction++;
        if (action instanceof Kicked)
            nKicked++;
    }
}
