package kakaotalk.parser;

import kakaotalk.Room;
import kakaotalk.action.*;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class Parser {
    CrlfLineReader reader;
    PatternActionMatcher actionMatcher = new PatternActionMatcher();
    String currentLine;  // 파서가 마지막으로 읽은 줄. 예외 상황을 확인하기 위해 이곳에 읽은 줄을 담음.
    boolean isEOF = false;
    // fstp: 대화기록 첫째 줄에 나타나는 패턴.
    // group(1): 방 제목
    // group(2): 방 인원
    final static Pattern fstp = compile("^(.+?) (\\d+) (?:님과 )?카카오톡 대화$");
    // sndp: 대화기록 두번째 줄에 나타나는 패턴.
    final static Pattern sndp = compile("^저장한 날짜 : ");
    // timep: 날짜 표시 패턴
    // 날짜만 있는 패턴(대화기록 2째줄 및 날짜가 바뀌는 줄)에는 뒤따르는 ', '가 나타나지 않는다.
    // group(1,2,3): 년, 월, 일
    // group(4): 오전|오후
    // group(5,6): 시, 분
    final static Pattern timep =
            compile("^(\\d{4,})년 (\\d{1,2})월 (\\d{1,2})일 (오전|오후) (\\d{1,2}):(\\d{1,2})(?:, )?");
    TimeProvider timeProvider =
            m -> {
                int year = Integer.parseInt(m.group(1)),
                    mon  = Integer.parseInt(m.group(2)),
                    day  = Integer.parseInt(m.group(3)),
                    hour = Integer.parseInt(m.group(5)),
                    min  = Integer.parseInt(m.group(6));
                if (m.group(4).equals("오후") && hour != 12)
                    hour += 12;
                return LocalDateTime.of(year, mon, day, hour, min);
            };

    public Parser(CrlfLineReader reader) {
        this.reader = reader;
        initMatchers();
    }

    public Parser(String filename) throws FileNotFoundException {
        reader = new DefaultCrlfLineReader(new FileInputStream(filename));
        initMatchers();
    }

    public String getLastLine() {
        return currentLine;
    }

    boolean parse1(Room room) throws IllegalStateException, IOException {
        if (isEOF) return false;
        try {
            currentLine = reader.readLine();
        } catch (EOFException e) {
            isEOF = true;
            return false;
        }
        if (currentLine.length() > 0) {
            // 날짜가 바뀔 때 빈 라인이 나타난다.
            // 이 파서는 날짜가 바뀌는 것을 유의미한 데이터로 여기지 않으므로 그런 경우는 스킵한다.
            try {
                actionMatcher.makeAction(currentLine, room);
            } catch (IllegalArgumentException e) { // TODO: MissingTimeException 만들것
                room.appendToLastChatting(currentLine);
            }
        }
        return true;
    }

    Room parseRoomInfo() throws IllegalStateException, IOException {
        currentLine = reader.readLine();
        Matcher m = fstp.matcher(currentLine);
        if (!m.matches())
            throw new IllegalStateException("Missing room info(1)");
        String title = m.group(1);
        int nEntry = Integer.parseInt(m.group(2));

        currentLine = reader.readLine();
        m = sndp.matcher(currentLine);
        if (!m.find())
            throw new IllegalStateException("Missing room info(2)");
        int timeStart = m.end();
        m = timep.matcher(currentLine).region(timeStart, currentLine.length());
        if (!m.find())
            throw new IllegalStateException("in line(2)\n\tUnexpected time pattern: " + currentLine);
        LocalDateTime begin = timeProvider.onMatch(m);

        return new Room(title, begin, nEntry);
    }

    public Room parse() {
        Room room;
        try {
            room = parseRoomInfo();
        } catch (EOFException e) {
            System.err.println("Parsing room info:\n\tUnexpected end of file");
            return null;
        } catch (IllegalStateException|IOException e) {
            System.err.println("Parsing room info:");
            System.err.println('\t' + e.getMessage());
            return null;
        }

        int lineCount = 2;
        boolean success = true;
        while (success) {
            lineCount++;
            try {
                success = parse1(room);
            } catch (IllegalStateException|IOException e) {
                System.err.println("Parsing line(" + lineCount + "):");
                System.err.println('\t' + e.getMessage());
            }
        }
        return room;
    }

    static final Pattern namep = compile("([^\\n]{1,20}?)님[,과을] ");
    // chopNames: 초대 메시지에서 'xxx님, yyy님과 zzz님을 '로 표기된 이름들을 떼어낸다.
    // 이름을 하나라도 발견하면 true를 반환한다.
    // res는 이름 발견 여부에 관계없이 빈 배열로 초기화된 후 발견된 이름들이 채워진다.
    public static boolean chopNames(String names, ArrayList<String> res) {
        Matcher m = namep.matcher(names);
        boolean found = false;
        res.clear();
        while (m.find()) {
            found = true;
            res.add(m.group(1));
        }
        return found;
    }

    private void initMatchers() {
        actionMatcher.setTimeMatcher (timep, timeProvider);
        actionMatcher.addMatcher(
                // 날짜가 바뀌는 패턴
                // 날짜를 뒤따르는 공백만 있다.
                compile("^\\s*$"),
                (room, time, matcher) -> {}
        );
        actionMatcher.addMatcher(
                // 일반 채팅 패턴
                // group(1): 대화명(반드시 한 줄에 나타나야 하며, 20자 이내이다.)
                // group(2): 대화내용
                compile("^([^\\n]{1,20}?) : (.*)$", Pattern.DOTALL),
                (room, time, m) ->
                    room.insertAction(m.group(1), new Chatting(time, m.group(2)), true)
        );
        actionMatcher.addMatcher(
                // 채팅방에 입장할 때 나타나는 패턴
                // group(1): 대화명
                compile("^(.*)님이 들어왔습니다.$"),
                (room, time, m) ->
                        room.insertAction(m.group(1), new Joining(time), false)
        );
        actionMatcher.addMatcher(
                // 채팅방에서 퇴장할 때 나타나는 패턴
                // group(1): 대화명
                compile("^(.*)님이 나갔습니다.$"),
                (room, time, m) ->
                        room.insertAction(m.group(1), new Leaving(time), true)
        );
        final ArrayList<String> invitees = new ArrayList<>();
        actionMatcher.addMatcher(
                // 사용자를 초대할 때 나타나는 패턴
                // group(1): 초대한 사람
                // group(2): 초대된 사람(들)
                compile("^(.*)님이 (.*)초대했습니다.$"),
                (room, time, m) -> {
                    String invitor = m.group(1);
                    if (!chopNames(m.group(2), invitees))
                        throw new IllegalStateException("Cannot resolve pattern");
                    for (String invitee : invitees) {
                        room.insertAction(invitor, new Inviting(time, invitee), true);
                        room.insertAction(invitee, new Invited(time, invitor), false);
                    }
                }
        );
        actionMatcher.addMatcher(
                // 포스트를 공유할 때 나타나는 패턴
                // group(1): 대화명
                compile("^(.*)님(이 포스트를 공유했습니다|의 포스트가 공유되었습니다).$"),
                (room, time, m) -> room.insertAction(m.group(1), new Posting(time), true)
        );
        actionMatcher.addMatcher(
                // 관리자가 추방할 때 나타나는 패턴
                // group(1): 추방된 유저의 대화명
                compile("^(.*)님을 내보냈습니다.$"),
                (room, time, m) -> {
                    room.insertAction(new Kicking(time, m.group(1)));
                    room.insertAction(m.group(1), new Kicked(time), true);
                }
        );
        actionMatcher.addMatcher(
                // 관리자가 글을 가렸을 때 나타나는 패턴
                compile("^채팅방 관리자가 메시지를 가렸습니다.$"),
                (room, time, m) -> room.insertAction(new Hiding(time))
        );
        actionMatcher.addMatcher(
                // 삭제된 메시지
                compile("^삭제된 메시지입니다.$"),
                (room, time, matcher) -> room.insertAction(new Canceling(time))
        );
        actionMatcher.addMatcher(
                // 대화를 기록한 사람이 채팅방에 입장할 때 나타나는 패턴
                // group(1): 대화명
                compile("^(.*)님이 들어왔습니다.\\n운영정책을 위반한 메시지로 신고 접수 시 카카오톡 이용에 제한이 있을 수 있습니다. 자세히 보기$"),
                (room, time, m) ->
                        room.insertAction(m.group(1), new Joining(time), false)
        );
        actionMatcher.addMatcher(
                // 방장이 바뀌었을 때 나타나는 패턴
                compile("^방장이 (?:.*)님에서 (?:.*)님으로 변경되었습니다.\\n이전 방장은 채팅방 참여자로 남게됩니다.$"),
                (room, time, matcher) -> room.insertAction(new Rebelling(time))
        );
    }
}
