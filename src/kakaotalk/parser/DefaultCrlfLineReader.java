package kakaotalk.parser;

import java.io.*;

public class DefaultCrlfLineReader implements CrlfLineReader {
    BufferedReader reader;
    boolean isEOF = false;

    public DefaultCrlfLineReader(InputStream in) {
        reader = new BufferedReader(new InputStreamReader(in));
    }

    @Override
    public String readLine() throws EOFException, IOException {
        if (isEOF) throw new EOFException();
        StringBuilder buf = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1) {
            if (c == '\r') {
                reader.read(); // 뒤따르는 '\n' 무시
                return buf.toString();
            }
            buf.append((char) c);
        }
        isEOF = true;
        return buf.toString();
    }
}
