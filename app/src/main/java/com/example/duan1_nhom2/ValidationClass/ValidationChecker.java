package com.example.duan1_nhom2.ValidationClass;

import android.content.Context;
import android.widget.Toast;

public class ValidationChecker {
    public static boolean isStringEmpty(Context context, String... strings){
        for (String i: strings){
            if (i.isEmpty()){
                Toast.makeText(context, "Không được để trống ô nhập!", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }
    public static boolean isEmailValid(Context context,String...emails){
        String regex = "\\w+@\\w+(.\\w+){1,2}";
        for (String i: emails){
            if (!i.matches(regex)){
                Toast.makeText(context, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
    public static boolean isPasswordValid(Context context, String...passwords){
        for (String i: passwords){
            if (i.length()<7){
                Toast.makeText(context, "Mật khẩu phải dài hơn 6 kí tự!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}
