package kakaotalk.parser;

import java.time.LocalDateTime;
import java.util.regex.Matcher;

interface TimeProvider {
    // find 완료한 Matcher 객체가 입력된다.
    LocalDateTime onMatch(Matcher matcher);
}
