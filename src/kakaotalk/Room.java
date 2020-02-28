package kakaotalk;

import kakaotalk.action.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;

public class Room implements Iterable<Member> {
    String title;              // 방제
    LocalDateTime begin, end;  // 대화 기록 시작/종료 시각
    final HashMap<String, Member> members; // 참여자 목록
    int nEntry;                // 채팅방 참여자 수
    int nHide = 0, nKick = 0,  // 관리자가 글을 가리고, 내보낸 횟수
            nRevel = 0;        // 방장이 바뀐 횟수
    int nCancel = 0;           // 이용자들이 자신의 글을 삭제한 횟수
    final Member root;         // 루트 멤버(AdminAction을 가지고 있는 멤버. 그 어떤 멤버도 아님.)
    Action lastAction;         // 마지막으로 삽입된 액션.

    public Room(String title, LocalDateTime begin, int nEntry) {
        this.title = title;
        this.begin = begin;
        this.nEntry = nEntry;
        members = new HashMap<>();
        root = new Member("", true, begin);
    }

    @Override
    public Iterator<Member> iterator() {
        return members.values().iterator();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void appendToLastChatting(String content) throws IllegalStateException {
        if (lastAction instanceof Chatting) {
            ((Chatting) lastAction).appendToContent(content);
        } else {
            throw new IllegalStateException("last action is not a chatting");
        }
    }

    public void insertAction(AdminAction action) {
        lastAction = (Action) action;
        root.insertAction((Action) action);
        if (action instanceof Kicking)
            nKick++;
        else if (action instanceof Hiding)
            nHide++;
        else if (action instanceof Canceling)
            nCancel++;
        else if (action instanceof Rebelling)
            nRevel++;
        end = ((Action) action).getTime();
    }

    public void insertAction(String name, Action action, boolean shouldBeExist) {
        lastAction = action;
        Member member = members.get(name);
        if (action instanceof EnterAction || action instanceof ExitAction) {
            boolean actionResult = !shouldBeExist;
            if (member == null) {
                member = new Member(name, actionResult, action.getTime());
                members.put(name, member);
            } else if (member.isExist() != shouldBeExist) {
                System.err.println
                        ("WARNING: There are users whose names('" + name + "') are duplicated");
            } else {
                member.setExist(actionResult);
            }
        } else {
            if (member == null) {
                member = new Member(name, shouldBeExist, action.getTime());
                members.put(name, member);
            } else if (member.isExist() != shouldBeExist) {
                System.err.println
                        ("WARNING: There are users whose names('" + name + "') are duplicated");
                member.setExist(shouldBeExist);
            }
        }
        member.insertAction(action);
        end = action.getTime();
    }

    // nullable
    public Member getMember(String name) {
        return members.get(name);
    }

    public int numOfEntry() {
        return nEntry;
    }

    public int numOfHide() {
        return nHide;
    }

    public int numOfKick() {
        return nKick;
    }

    public int numOfRevel() {
        return nRevel;
    }

    public int numOfCancel() {
        return nCancel;
    }

    public Member getRoot() {
        return root;
    }
}
