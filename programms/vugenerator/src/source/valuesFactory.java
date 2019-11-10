package source;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class valuesFactory {
    private String letters = "abcdefghijklmnopqrstuvwxyz";
    private String nums = "0123456789";
    private String[] boxes = {"@mail.ru", "@inbox.ru", "@gmail.com"};
    private StringBuilder values;
    private int quantity;
    private String separator;
    private int columns;

    public valuesFactory(int quantity, String separator, int columns) {
        this.quantity = quantity;
        this.columns = columns;
        this.separator = separator;
    }

    void generateRandomUsers (int begin, int end, boolean letters, boolean nums, boolean mail) {
        StringBuilder data = new StringBuilder();
        StringBuilder result = new StringBuilder();
        boolean onlyMail = false;

        if (nums)
            data.append(this.nums);
        if (letters)
            data.append(this.letters);
        if (!nums & !letters)
            if (mail) {
                data.append(this.nums);
                data.append(this.letters);
                onlyMail = true;
            } else {
                return;
            }

        String[] dataArr = data.toString().split("");
        for (int i = 0; i < quantity; i++) {
            for (int j = 0; j < columns; j++) {
                int quantity = begin + new Random().nextInt(end);

                for (int k = 0; k < quantity; k++) {
                    int random = new Random().nextInt(dataArr.length);
                    result.append(dataArr[random]);
                }
                if (onlyMail || j == columns - 1) {
                    result.append(boxes[new Random().nextInt(boxes.length)]);
                } else {
                    result.append(separator);
                }
            }
            result.append("\n");
        }
        values = result;
    }

    void saveToFile(String path) {
        try(FileOutputStream fos = new FileOutputStream(path))
        {
            byte[] buffer = values.toString().getBytes();

            fos.write(buffer, 0, buffer.length);
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    public String getNums() {
        return nums;
    }

    public void setNums(String nums) {
        this.nums = nums;
    }

    public String[] getBoxes() {
        return boxes;
    }

    public void setBoxes(String[] boxes) {
        this.boxes = boxes;
    }

    public StringBuilder getValues() {
        return values;
    }

    public void setValues(StringBuilder values) {
        this.values = values;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }
}
