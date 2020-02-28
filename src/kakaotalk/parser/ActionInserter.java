package kakaotalk.parser;

import kakaotalk.Room;

import java.time.LocalDateTime;
import java.util.regex.Matcher;

interface ActionInserter {
    // matches 완료한 Matcher 객체가 입력된다.
    void onMatch(Room room, LocalDateTime time, Matcher matcher) throws IllegalStateException;
}
