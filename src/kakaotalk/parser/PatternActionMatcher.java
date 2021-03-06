package kakaotalk.parser;

import kakaotalk.Room;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 읽은 줄의 패턴에 따라 액션 객체를 생성한다.
class PatternActionMatcher {
    LinkedList<OnMatch> tasks = new LinkedList<>();
    Pattern timePattern;
    TimeProvider timeProvider;

    void setTimeMatcher(Pattern pattern, TimeProvider provider) {
        timePattern = pattern;
        timeProvider = provider;
    }

    void addMatcher(Pattern pattern, ActionInserter provider) {
        tasks.add(new OnMatch(pattern, provider));
    }

    void makeAction(String s, Room room) throws IllegalStateException, IllegalArgumentException {
        Matcher m = timePattern.matcher(s);
        if (!m.find())
            throw new IllegalArgumentException("Missing time: " + s);
        int messageStart = m.end();
        LocalDateTime time = timeProvider.onMatch(m);
        for (OnMatch task : tasks) {
            m = task.pattern.matcher(s);
            m.region(messageStart, s.length());
            if (m.matches()) {
                task.provider.onMatch(room, time, m);
                return;
            }
        }
        throw new IllegalStateException("Nothing matches: " + s);
    }
}
