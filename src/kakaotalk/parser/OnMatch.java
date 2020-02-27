package kakaotalk.parser;

import java.util.regex.Pattern;

class OnMatch {
    final Pattern pattern;
    final ActionProvider provider;
    OnMatch(Pattern pattern, ActionProvider provider) {
        this.pattern = pattern;
        this.provider = provider;
    }
}
