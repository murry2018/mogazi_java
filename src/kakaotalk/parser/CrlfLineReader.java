package kakaotalk.parser;

import java.io.EOFException;

// TODO: DefaultCrlfLineReader 클래스 구현
public interface CrlfLineReader {
    String readLine() throws EOFException;
}
