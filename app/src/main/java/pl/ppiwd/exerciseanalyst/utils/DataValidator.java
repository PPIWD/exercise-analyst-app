package pl.ppiwd.exerciseanalyst.utils;

import android.text.TextUtils;
import android.util.Patterns;

public final class DataValidator {
    public static boolean isEmailValid(final String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
}
