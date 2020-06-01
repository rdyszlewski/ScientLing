package com.dyszlewskiR.edu.scientling.service.net.upload;

class ChunkBuffer{
    private char[] mBuffer;
    private int mChunkSize;
    private int mBufferPosition;
    private String mTempString;

    public ChunkBuffer(int chunkSize){
        mChunkSize = chunkSize;
        mBuffer = new char[mChunkSize];
        mBufferPosition = 0;
    }

    public void addString(String string) {
        for(int stringPosition = 0; stringPosition < string.length(); stringPosition++){
            mBuffer[mBufferPosition] = string.charAt(stringPosition);
            if(++mBufferPosition == mChunkSize){
                mTempString = string.substring(stringPosition+1, string.length());
                return;
            }
        }
    }

    public boolean isFull(){
        return mBufferPosition == mChunkSize;
    }

    public boolean isEmpty(){
        return mBufferPosition == 0;
    }

    public void next() {
        mBufferPosition = 0;
        addString(mTempString);
        mTempString = null;
    }

    public void reset(){
        mBufferPosition = 0;
        mTempString = null;
    }

    public char[] getBuffer() {return mBuffer;}

    public String getString(){return String.valueOf(mBuffer,0, mBufferPosition);}
}