package kakaotalk.parser.tests;

import kakaotalk.parser.CrlfLineReader;
import kakaotalk.parser.DefaultCrlfLineReader;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class CrlfLineReaderTest {
    public static void main(String[] args) {
        CrlfLineReader reader;
        try {
            reader = new DefaultCrlfLineReader(new FileInputStream("C:\\examples\\KakaoTalkChats.txt"));
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            return;
        }

        int lineCount = 0;
        Scanner cmdReader = new Scanner(System.in);
        do {
            try {
                System.out.printf("[%d] %s\n", ++lineCount, reader.readLine());
            } catch (EOFException e) {
                System.out.println("End of file.");
                break;
            } catch (IOException e) {
                System.err.println(e.getMessage());
                break;
            }
            System.out.print("CMD> ");
        } while (!cmdReader.nextLine().equals("quit"));
    }
}
