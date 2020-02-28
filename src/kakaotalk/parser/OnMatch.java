package kakaotalk.parser;

import java.util.regex.Pattern;

class OnMatch {
    final Pattern pattern;
    final ActionInserter provider;
    OnMatch(Pattern pattern, ActionInserter provider) {
        this.pattern = pattern;
        this.provider = provider;
    }
}
