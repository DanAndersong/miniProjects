package logic;

public class Calculator {
    private String[] opers = {"plus","minus","del","mult"};
    public String calculate (String oper, String num1, String num2) {
        int result = 0;

        if (isNum(num1) && isNum(num2)){
            int a;
            int b;
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

    private boolean isNum (String s) {
        try {
            double d = Double.parseDouble(s);
        } catch (Exception e) {
            return false;
        }
        return true;
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
