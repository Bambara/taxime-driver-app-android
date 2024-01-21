package com.ynr.taximedriver.validation;

public class Formvalidation {
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String MOBILE_NUMBER = "mobile number";
    public static final String NIC = "nic";

//    public static boolean textValidation(String type, String text) {
//        switch (type) {
//            case NAME:
//                return text.length() > 0;
//                break;
//            case EMAIL:
//
//        }
//    }

    public static boolean isNIC(String nicChecker) {
        final String NIC_PATTERN = "[0-9]{0}\\d{9}+v|[0-9]{0}\\d{9}+x|[0-9]{0}\\d{9}+V|[0-9]{0}\\d{9}+X|[0-9]{0}\\d{12}";
        if (nicChecker.matches(NIC_PATTERN)) return true;
        else return false;
    }

    public static boolean isEmail(String emailChecker) {
        final String EMAIL_PATTERN = "(?:[a-z0-9!#$%&'*+\\=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+\\=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        if (emailChecker.matches(EMAIL_PATTERN)) return true;
        else return false;
    }

    public static boolean isMobileNumber(String mobileChecker) {
        final String MOBILE_NUMBER_PATTERN = "070+[0-9]{0}\\d{7}|071+[0-9]{0}\\d{7}|072+[0-9]{0}\\d{7}|75+[0-9]{0}\\d{7}|076+[0-9]{0}\\d{7}|077+[0-9]{0}\\d{7}|078+[0-9]{0}\\d{7}|70+[0-9]{0}\\d{7}|71+[0-9]{0}\\d{7}|72+[0-9]{0}\\d{7}|075+[0-9]{0}\\d{7}|76+[0-9]{0}\\d{7}|77+[0-9]{0}\\d{7}";
        if (mobileChecker.matches(MOBILE_NUMBER_PATTERN)) return true;
        else return false;
    }

    public static boolean isName(String nameChecker) {
        final String NAME_PATTERN = "([A-Za-z0-9\\. -]+)";
        if (nameChecker.matches(NAME_PATTERN)) return true;
        else return false;
    }
}
