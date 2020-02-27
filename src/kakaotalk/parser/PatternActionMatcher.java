package kakaotalk.parser;

import kakaotalk.action.Action;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 읽은 줄의 패턴에 따라 액션 객체를 생성한다.
class PatternActionMatcher {
    LinkedList<OnMatch> tasks;
    Pattern timePattern;
    TimeProvider timeProvider;

    void setTimeMatcher(Pattern pattern, TimeProvider provider) {
        timePattern = pattern;
        timeProvider = provider;
    }

    void addMatcher(Pattern pattern, ActionProvider provider) {
        tasks.add(new OnMatch(pattern, provider));
    }

    Action makeAction(String s) throws IllegalStateException {
        Matcher m = timePattern.matcher(s);
        if (!m.find())
            throw new IllegalStateException("Missing time: " + s);
        int messageStart = m.end();
        LocalDateTime time = timeProvider.onMatch(m);
        for (OnMatch task : tasks) {
            m = task.pattern.matcher(s);
            m.region(messageStart, s.length());
            if (m.matches())
                return task.provider.onMatch(time, m);
        }
        throw new IllegalStateException("Nothing matches: " + s);
    }
}
