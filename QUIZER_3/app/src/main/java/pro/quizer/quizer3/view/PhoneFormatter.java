package pro.quizer.quizer3.view;

public class PhoneFormatter {
    String cached = "";
    String phone = "";
    int selection = 0;

    public void beforeTextChanged(int start, int count) {
        if (start == 0 && count == 0) {
            cached = "";
        }
    }

    public int getSelection() {
        return selection;
    }

    public void add(String s,int position) {
        onTextChanged(s,position,0,1);
    }

    public void delete(String s,int position) {
        onTextChanged(s,position,1,0);
    }

    public void onTextChanged(String s, int cursorPosition, int before, int count) {
        if (!s.isEmpty()
                && !"+7".equals(s)
                && !"+(".equals(s)
                && !"+7(".equals(s)) {
            String value = cleaned(s);
            String formatted = formatted(value);
            if (formatted.length() > cursorPosition) {
                Character c = formatted.charAt(cursorPosition);
                String deleted = c.toString();
                if (before == 1 && count == 0 && (deleted.equals(")") || deleted.equals("-"))) {
                    StringBuilder sb = new StringBuilder(formatted);
                    sb.deleteCharAt(cursorPosition - 1);
                    cursorPosition--;
                    value = cleaned(sb.toString());
                    formatted = formatted(value);

                }
            }

            if (s.length() < 4) {
                phone = "+7(" + value;
                selection = 4;
                return;
            }
            if (!value.isEmpty() && !value.equals(cached)) {
                cached = value;
                phone = formatted;
                if (before == 0 && count == 1) { //add
                    switch (cursorPosition) {
                        case 0:
                            cursorPosition = formatted.length();
                            break;
                        case 6:
                        case 10:
                        case 13:
                            cursorPosition += 2;
                            break;
                        default:
                            cursorPosition++;
                            break;
                    }
                    if (cursorPosition == 0) {
                        cursorPosition = formatted.length();
                    }
                }
                if (before == 1 && count == 0) { //delete
                    switch (cursorPosition) {
                        case 3:
                            cursorPosition = 3;
                            break;
                        case 7:
                        case 11:
                        case 14:
                            cursorPosition--;
                            break;
                    }
                }
                selection = cursorPosition;
            }
            if (value.equals(cached) && !s.contains("+7(")) {
                phone = formatted(cached);
                selection = 3;

            }
            if (value.isEmpty()) {
                phone = "+7(";
                selection = 2;
            }
        } else if(!"+7(".equals(phone)) {
            phone = "+7(";
            selection = 3;
        }
    }

    String formatted(String value) {
        StringBuffer sb = new StringBuffer("+7(");
        if (value.length() <= 3) {
            sb.append(value);
        } else if (value.length() > 3 && value.length() <= 6) {
            sb.append(value.substring(0, 3))
                    .append(")");
            sb.append(value.substring(3));
        } else if (value.length() > 6 && value.length() <= 8) {
            sb.append(value.substring(0, 3)).append(")");
            sb.append(value.substring(3, 6));
            sb.append("-");
            sb.append(value.substring(6));
        } else if (value.length() > 8 && value.length() <= 10) {
            sb.append(value.substring(0, 3)).append(")");
            sb.append(value.substring(3, 6));
            sb.append("-");
            sb.append(value.substring(6, 8));
            sb.append("-");
            sb.append(value.substring(8));
        }
        if ("+7(".equals(sb.toString())) {
            sb = new StringBuffer();
        }
        return sb.toString();}

    String cleaned(String s) {
        String value = s.toString()
                .replace("+7(", "")
                .replace("+7", "")
                .replace("+", "")
                .replace("(", "")
                .replace(")", "")
                .replace("-", "");
        return value;
    }

    public String getPhone() {
        return phone;
    }

    public void setCached(String cached){
        this.cached = cached;
    }
}
