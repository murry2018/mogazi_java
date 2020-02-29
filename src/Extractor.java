import kakaotalk.Member;
import kakaotalk.Room;
import kakaotalk.action.Action;
import kakaotalk.action.Chatting;
import kakaotalk.parser.Parser;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Extractor {
    private static Scanner reader = new Scanner(System.in);
    // readFile: System.in으로부터 파일 명을 입력받아 Parser 객체를 생성한다.
    static Parser readMakeParser() {
        boolean readGood = false;
        kakaotalk.parser.Parser parser = null;
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
        boolean readGood = false;
        FileOutputStream out = null;
        while (!readGood) {
            System.out.print("출력 파일의 경로: ");
            String path = reader.nextLine();
            try {
                out = new FileOutputStream(path);
                readGood = true;
            } catch (IOException e) {
                System.out.println("파일을 열 수 없습니다. 발생한 오류:");
                System.out.println(e.getMessage());
                System.out.println("다시 입력하세요.");
            }
        }
        return out;
    }
    // Extractor.main: 카카오톡 대화내역에서 특정 유저의 대화기록만 추출한다.
    public static void main(String[] args) {
        Parser parser = readMakeParser();
        Room room = parser.parse();
        ArrayList<String> names = getMemberNames(room, true);
        boolean end = false;
        while (!end) {
            Member member = readSelectMember(room, names);
            FileOutputStream out = readOpenFile();
            for (Action action: member.getActions()) {
                if (action instanceof Chatting) {
                    try {
                        out.write(action.getContent().getBytes());
                    } catch (IOException e) {
                        System.out.println("다음 행에서 중단됨: ");
                        System.out.println(action.getContent());
                        System.out.println("발생한 에러: ");
                        System.out.println(e.getMessage());
                        System.exit(-1);
                    }
                }
            }
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                System.out.println("파일 닫기에 실패했습니다. 발생한 에러:");
                System.out.println(e.getMessage());
                System.exit(-1);
            }
            System.out.print("다른 유저로 계속? (y/other) ");
            String keepGoing = reader.nextLine();
            if (!keepGoing.equals("y") && !keepGoing.equals("Y")) {
                end = true;
            }
        }
    }
}
