package kakaotalk.parser.tests;

import java.io.FileNotFoundException;

public class ParserTest {
    public static void main(String[] args) {
        final String file = "C:\\examples\\KakaoTalkChats.txt";
        kakaotalk.parser.Parser parser;
        try {
            parser = new kakaotalk.parser.Parser(file);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            return;
        }

        kakaotalk.Room room;
        room = parser.parse();
        System.out.println("Title: " + room.getTitle());
        System.out.println("Entries: " + room.numOfEntry());

        for (kakaotalk.Member member : room) {
            if (member.isExist())
                System.out.println(member.getName() + ": " + member.numOfAction());
        }
    }
}
