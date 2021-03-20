package pl.ppiwd.exerciseanalyst.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;

public class WipeDatabase {
    public static void wipe(Context context, String databaseName) {
        File databases = new File(context.getApplicationInfo().dataDir + "/databases");
        File db = new File(databases, databaseName);
        if (db.delete())
            Log.i("WipeDatabase::wipe()", "Database deleted");
        else
            Log.i("WipeDatabase::wipe()", "Failed to delete database");

        File journal = new File(databases, databaseName + "-journal");
        if (journal.exists()) {
            if (journal.delete())
                Log.i("WipeDatabase::wipe()", "Database journal deleted");
            else
                Log.i("WipeDatabase::wipe()", "Failed to delete database journal");
        }
    }
}
