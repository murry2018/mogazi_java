import kakaotalk.Member;
import kakaotalk.action.Action;
import kakaotalk.action.ActiveAction;

import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;

public class Mogazi {
    static class MutableInt {
        int val = 0;

        public int get() {
            return val;
        }
        public void inc () {
            val += 1;
        }
    }
    static class DateVal {
        LocalDate date;
        int val;
        DateVal(LocalDate date, int val) {
            this.date = date;
            this.val = val;
        }
    }
    static final int    NQUARTER = 15;       // 상위 n명, 하위 n명의 n
    static final int    NDAYPRINT = 5;       // 말을 많이/적게 했던 x일의 x
    static final int    NMEMB_TOSEP = 7;     // 한 줄에 출력할 이름 수
    public static void main(String[] Args) {
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

        // 채팅방의 통계 정보 수집
        kakaotalk.Room room = parser.parse();
        LinkedList<kakaotalk.Member> members = new LinkedList<>(); // 원하는 기준대로 정렬하여 쓰기 위함.
        int   totalActs = 0;                // 총 활동 수
        int[] actByTimes = new int[24];     // 시간 별 활동
        int[] actByDayOfWeek = new int[7];  // 요일 별 활동(0:MON .. 6:SUN)
        HashMap<LocalDate, MutableInt> actByDay = new HashMap<>(); // 일별 활동 횟수
        for (kakaotalk.Member member : room) {
            if (member.isExist())
                members.add(member);
            ArrayList<Action> actions = member.getActions();
            for (Action action : actions) {
                if (action instanceof ActiveAction) {
                    totalActs++;
                    LocalDateTime time = action.getTime();
                    actByTimes[time.getHour()]++;
                    actByDayOfWeek[time.getDayOfWeek().getValue()-1]++;

                    LocalDate date = time.toLocalDate();
                    MutableInt val = actByDay.get(date);
                    if (val == null) {
                        val = new MutableInt();
                        actByDay.put(date, val);
                    }
                    val.inc();
                }
            }
        }
        // 날짜별 통계
        LinkedList<DateVal> sortedActByDay = new LinkedList<>();
        for (LocalDate date : actByDay.keySet())
            sortedActByDay.add(new DateVal(date, actByDay.get(date).get()));
        sortedActByDay.sort((d1, d2) -> d1.val - d2.val); // 오름차순 정렬됨
        // 킥/가리기 관련
        boolean isOppressed = room.numOfHide() != 0 || room.numOfKick() != 0;

        System.out.printf("%s (인원: %d 명) ", room.getTitle(), room.numOfEntry());
        System.out.println(room.getDays() + "일 간의 통계\n");
        System.out.println(room.getBegin() + " ~ " + room.getEnd());

        if (isOppressed) {
            System.out.println();
            System.out.println("=== 완장의 만행 ===");
            System.out.printf("입막음: %d회, 추방: %d회\n", room.numOfHide(), room.numOfKick());
        }

        System.out.println();
        System.out.println("=== 기타 통계 ===");
        System.out.printf("삭제된 메시지: %d개\n", room.numOfCancel());

        System.out.println();
        System.out.println("=== 말을 가장 많이 했던 날들 ===");
        Iterator<DateVal> dateValIterator = sortedActByDay.descendingIterator();
        for (int i = 0; i < NDAYPRINT; i++) {
            if (!dateValIterator.hasNext())
                break;
            DateVal dateVal = dateValIterator.next();
            System.out.println(dateVal.date + " : " + dateVal.val + " 회");
        }

        System.out.println();
        System.out.println("=== 말을 가장 적게 했던 날들 ===");
        dateValIterator = sortedActByDay.iterator();
        for (int i = 0; i < NDAYPRINT; i++) {
            if (!dateValIterator.hasNext())
                break;
            DateVal dateVal = dateValIterator.next();
            System.out.println(dateVal.date + " : " + dateVal.val + " 회");
        }

        System.out.println();
        System.out.println("=== 요일별 활동비율 ===");
        for (int i = 0; i < 7; i++) {
            String name = DayOfWeek.of(i+1).getDisplayName(TextStyle.SHORT, Locale.KOREAN);
            System.out.printf("%s: %.1f%% / ", name, 100.0 * ((double) actByDayOfWeek[i] / totalActs));
        }
        System.out.println();

        System.out.println();
        System.out.println("=== 시간대별 활동비율 ===");
        System.out.println("[오전]");
        for (int i = 0; i < 12; i++) {
            if (i > 0 && i % 3 == 0) System.out.println();
            System.out.printf("%d시 : %.1f%% / ", i, 100.0 * ((double) actByTimes[i] / totalActs));
        }
        System.out.println();
        System.out.println("[오후]");
        for (int i = 12; i < 24; i++) {
            if (i > 12 && i % 3 == 0) System.out.println();
            System.out.printf("%d시 : %.1f%% / ", i, 100.0 * ((double) actByTimes[i] / totalActs));
        }
        System.out.println();

        System.out.println();
        System.out.printf("=== 말을 많이 한 상위 %d명 ===\n", NQUARTER);
        members.sort((m1, m2) -> m1.numOfAction() - m2.numOfAction());
        Iterator<Member> memberIterator = members.descendingIterator();
        int lineCount = 0;
        while (memberIterator.hasNext()) {
            Member member = memberIterator.next();
            System.out.printf("[%3d] %s :: %d회\n", ++lineCount, member.getName(), member.numOfAction());
            if (lineCount == NQUARTER)
                break;
        }

        System.out.println();
        System.out.printf("=== 말을 적게 한 상위 %d명 ===\n", NQUARTER);
        lineCount = 0;
        for (Member member: members) {
            System.out.printf("[%3d] %s :: %d회\n", ++lineCount, member.getName(), member.numOfAction());
            if (lineCount == NQUARTER)
                break;
        }

        if (isOppressed) {
            System.out.println();
            System.out.println("=== 추방을 많이 당한 순위 ===");
            members.sort((m1, m2) -> m2.numOfKicked() - m1.numOfKicked());
            lineCount = 0;
            for (Member member : members) {
                if (member.numOfKicked() == 0)
                    break;
                System.out.printf("[%3d] %s :: %d회\n", ++lineCount, member.getName(), member.numOfKicked());
            }
        }

        System.out.println();
        System.out.printf("=== 확인된 사용자(가나다순, %d 명) ===\n", members.size());
        members.sort((m1, m2) -> m1.getName().compareTo(m2.getName()));
        int count = 0;
        for (Member member: members) {
            System.out.printf("[%s] ", member.getName());
            if (++count % NMEMB_TOSEP == 0)
                System.out.println();
        }
    }
}
