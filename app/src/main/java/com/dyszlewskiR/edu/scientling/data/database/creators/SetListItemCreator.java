package com.dyszlewskiR.edu.scientling.data.database.creators;

import android.database.Cursor;

import com.dyszlewskiR.edu.scientling.data.database.tables.SetsTable;
import com.dyszlewskiR.edu.scientling.models.others.SetListItem;

public class SetListItemCreator {

    public static SetListItem createFromCursor(Cursor cursor) {
        if(cursor != null){
            SetListItem item = new SetListItem();
            long globalId = -1;
            String uploadingUser = null;
            for(int i=0; i<cursor.getColumnCount(); i++){
                switch (cursor.getColumnName(i)){
                    case SetsTable.SetsColumns.ID:
                        item.setId(cursor.getLong(i)); break;
                    case SetsTable.SetsColumns.NAME:
                        item.setName(cursor.getString(i));
                    case SetsTable.SetsColumns.GLOBAL_ID:
                        globalId = cursor.getLong(i); break;
                    case SetsTable.SetsColumns.UPLOADING_USER:
                        uploadingUser = cursor.getString(i);
                }
                if(globalId>0){
                    if(uploadingUser==null){
                        item.setServerState(SetListItem.ServerState.DOWNLOADED);
                    } else {
                        item.setServerState(SetListItem.ServerState.UPLOADED);
                    }
                } else {
                    item.setServerState(SetListItem.ServerState.NONE);
                }

            }
            return item;
        }
        return null;
    }
}
