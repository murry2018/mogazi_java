package kakaotalk.parser;

import java.io.IOException;

public interface CrlfLineReader {
    String readLine() throws IOException;
}
