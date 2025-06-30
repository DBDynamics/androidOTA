package cn.wch.bleota.scan;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

public class ExTool {
    public static String getUriFileName(Context context,Uri uri){
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        String filename=null;
        if(cursor!=null){
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if(columnIndex>=0){
                filename= cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return filename;
    }
}
