package com.dyszlewskiR.edu.scientling.service.net.upload;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


public class ChunkStreamWriter {
    private ChunkBuffer mChunkBuffer;
    private OutputStreamWriter mWriter;

    public ChunkStreamWriter(int chunkSize, OutputStream outputStream){
        mChunkBuffer = new ChunkBuffer(chunkSize);
        mWriter = new OutputStreamWriter(outputStream);
    }

    public void write(String value) throws IOException {
        mChunkBuffer.addString(value);
        while (mChunkBuffer.isFull()){
            Log.d(getClass().getSimpleName(), new String(mChunkBuffer.getBuffer()));
            mWriter.write(mChunkBuffer.getBuffer());
            mWriter.flush();
            mChunkBuffer.next();
        }
    }

    /**
     * Metoda zapisująca do strumienia zawartość bufora, a następnie resetująca jesgo stan
     * @throws IOException
     */
    public void writeBuffer() throws IOException {
        if(!mChunkBuffer.isEmpty()){
            mWriter.write(mChunkBuffer.getString());
            mWriter.flush();
            mChunkBuffer.reset();
        }
    }

    public void close() throws IOException {
        mWriter.close();
    }
}
