package kakaotalk.parser;

import kakaotalk.action.Action;

import java.time.LocalDateTime;
import java.util.regex.Matcher;

interface ActionProvider {
    // matches 완료한 Matcher 객체가 입력된다.
    Action onMatch(LocalDateTime time, Matcher matcher);
}
