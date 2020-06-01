package com.dyszlewskiR.edu.scientling.service.net.deleting;

public interface IDeletingTask {
    void delete(long globalId, boolean deleteDatabase, boolean deleteImages, boolean deleteRecords);
}
