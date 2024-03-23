package com.subject.segmenttation;

import android.content.Context;
import android.net.Uri;

public class Utils {
    public static Uri getUriFromDrawable(Context context, int drawableId) {
        Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + drawableId);
        return uri;
    }
}
