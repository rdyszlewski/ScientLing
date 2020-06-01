package com.dyszlewskiR.edu.scientling.service.net.deleting;

import android.content.Context;
import android.content.Intent;

import com.dyszlewskiR.edu.scientling.app.LingApplication;

public class DeletingTask implements IDeletingTask {

    @Override
    public void delete(long globalId, boolean deleteDatabase, boolean deleteImages, boolean deleteRecords) {
        Context context = LingApplication.getInstance().getBaseContext();
        Intent intent = new Intent(context, DeletingSetService.class);
        intent.putExtra("set",globalId);
        intent.putExtra("database", deleteDatabase);
        intent.putExtra("images", deleteImages);
        intent.putExtra("records", deleteRecords);
        context.startService(intent);
    }
}
