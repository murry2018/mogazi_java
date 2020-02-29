import kakaotalk.Member;
import kakaotalk.Room;
import kakaotalk.parser.Parser;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Extractor {
    // readFile: System.in으로부터 파일 명을 입력받아 Parser 객체를 생성한다.
    static Parser readMakeParser() {
        boolean readGood = false;
        kakaotalk.parser.Parser parser = null;
        Scanner reader = new Scanner(System.in);
        while (!readGood) {
            System.out.print("카카오톡 대화 내역 파일의 경로: ");
            String path = reader.nextLine();
            try {
                parser = new kakaotalk.parser.Parser(path);
                readGood = true;
            } catch (FileNotFoundException e) {
                System.out.println("그런 파일은 존재하지 않습니다. 다시 입력해주세요.");
                readGood = false;
            }
        }
        return parser;
    }
    // getMemberNames: room에 있는 모든 이름을 가져옴
    // shouldBeExist가 참이면 방에 있는 것으로 기록된 유저만 가져온다.
    static ArrayList<String> getMemberNames(Room room, boolean shouldBeExist) {
        ArrayList<String> names = new ArrayList<>();
        for (Member member : room) {
            if (member.isExist())
                names.add(member.getName());
            else if (!shouldBeExist)
                names.add(member.getName());
            // else skip
        }
        return names;
    }
    // readSelectMember: System.in으로부터 유저명을 받아 Member 객체를 반환한다.
    static Member readSelectMember(Room room, ArrayList<String> names) {
        System.out.println("확인된 유저의 목록: ");
        int count = 0;
        for (String name : names) {
            System.out.printf("%d: %s\n", count++, name);
        }
        int index = 0;
        boolean readGood = false;
        Scanner reader = new Scanner(System.in);
        while (!readGood) {
            System.out.print("대화 기록을 추출할 유저의 번호: ");
            index = reader.nextInt();
            if (0 <= index && index < names.size()) {
                readGood = true;
            } else {
                System.out.println("번호가 올바르지 않습니다. 다시 입력하세요.");
            }
        }
        return room.getMember(names.get(index));
    }
    // readOpenFile: System.in으로부터 파일명을 입력받아 출력 스트림을반환한다.
    static FileOutputStream readOpenFile() {

    }
    // Extractor.main: 카카오톡 대화내역에서 특정 유저의 대화기록만 추출한다.
    public static void main(String[] args) {
        Parser parser = readMakeParser();
        Room room = parser.parse();
        ArrayList<String> names = getMemberNames(room, true);
        boolean end = false;
        while (!end) {
            Member member = readSelectMember(room, names);

        }
    }
}
