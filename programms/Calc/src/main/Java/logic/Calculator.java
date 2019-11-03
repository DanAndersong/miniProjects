package logic;

public class Calculator {
    private String[] opers = {"plus","minus","del","mult"};


    // 500/h

    public String calculate (String oper, String num1, String num2) {
        int result = 0;

        if (isNums(num1, num2)){
            int a, b;
            try {
                a = Integer.parseInt(num1);
                b = Integer.parseInt(num2);
            }catch (NumberFormatException e) {
                return "Too much";
            }catch (Exception e) {
                return "Unchecked error";
            }
            if (isOper(oper)) {
                if (oper.equals(opers[0]))
                    result = a + b;
                if (oper.equals(opers[1]))
                    result = a - b;
                if (oper.equals(opers[2]))
                    result = a / b;
                if (oper.equals(opers[3]))
                    result = a * b;
            } else {
                return "Operator error";
            }
        }else {
            return  "Values contains letters";
        }
        return String.valueOf(result);
    }

    private boolean isNums (String ... s) {
        int nums = 0;

        for (int i = 0; i < s.length; i++) {
            try {
                double d = Double.parseDouble(s[i]);
                nums++;
            } catch (Exception e) {
                return false;
            }
        }
        return nums == s.length;
    }

    private boolean isOper (String s) {
        boolean result = false;

        for (int i = 0; i < opers.length; i++) {
            if (opers[i].equals(s)) {
                result = true;
            }
        }
        return result;
    }
}
