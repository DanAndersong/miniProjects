import java.io.FileWriter;
import java.io.IOException;

public class EightSymbols {
    public static void main(String[] args) throws IOException {
        FileWriter nFile = new FileWriter("file1.txt");
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder stringBuilderNull = new StringBuilder();
        stringBuilderNull.append("0");
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 10; j++) {
                stringBuilder.append(stringBuilderNull.toString() + j + "\n");
            }
            stringBuilderNull.append("0");
        }
        nFile.write(stringBuilder.toString());

        for (int i = 0; i < 100_000_000; i++) {
            nFile.write(String.valueOf(i));
            nFile.write("\n");
            nFile.write(String.valueOf(i));
        }
        nFile.close();
    }
}
