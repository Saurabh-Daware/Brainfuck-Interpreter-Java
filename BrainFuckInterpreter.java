import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class BrainFuckInterpreter {
    public static void main(String[] args) {

        try {
            String code = new String(Files.readAllBytes(Paths.get(".\\" + args[0])));
            checkSyntax(code);
            interpret(code);
        } catch (SyntaxError e) {
            System.err.println(e);
        } catch (FileNotFoundException e) {
            System.err.println("Input file not found");
        } catch (IOException e) {
            System.err.println("Problem with File or File input");
        }
    }

    private static void checkSyntax(String code) throws SyntaxError {
        Stack<Integer> stack = new Stack<>();
        int index = 0;

        for (char c : code.toCharArray()) {
            if (c == '[')
                stack.push(index);
            else if (c == ']' && stack.isEmpty())
                throw new LoopNotStartedException("Syntax error: " + index + " Tried closing non existing loop.");
            else if (c == ']')
                stack.pop();
            index++;
        }

        if (!stack.isEmpty())
            throw new LoopNotClosedException("Loop started at index" + stack.peek() + " was never closed");
    }

    static void interpret(String code) {

        byte[] data = new byte[30_000];
        int codeIndex = 0, dataIndex = 0, codeLength = code.length();
        Scanner sc = new Scanner(System.in);
        Stack<Integer> stack = new Stack<>();

        while (codeIndex < codeLength) {
            char presentOp = code.charAt(codeIndex);

            switch (presentOp) {
                case '+':
                    if (data[dataIndex] == 255)
                        data[dataIndex] = 0;
                    else
                        ++data[dataIndex];
                    break;
                case '-':
                    if (data[dataIndex] == 0)
                        data[dataIndex] = (byte) 255;
                    else
                        --data[dataIndex];
                    break;
                case '>':
                    ++dataIndex;
                    break;
                case '<':
                    --dataIndex;
                    break;
                case '.':
                    System.out.print((char) data[dataIndex]);
                    break;
                case ',':
                    data[dataIndex] = (byte) sc.nextLine().charAt(0);
                    break;
                case '[':
                    if (data[dataIndex] < 1) {
                        while (code.charAt(codeIndex) != ']')
                            codeIndex++;
                    } else
                        stack.push(codeIndex);
                    break;
                case ']':
                    if (data[dataIndex] != 0) {
                        codeIndex = stack.peek();
                    } else
                        stack.pop();
                    break;
            }
            ++codeIndex;
        }
        sc.close();
    }
}

class LoopNotClosedException extends SyntaxError {
    public LoopNotClosedException(String message) {
        super(message);
    }
}

class LoopNotStartedException extends SyntaxError {
    public LoopNotStartedException(String message) {
        super(message);
    }
}

class SyntaxError extends Throwable {
    public SyntaxError(String message) {
        super(message);
    }
}