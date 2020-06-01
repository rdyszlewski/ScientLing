package com.dyszlewskiR.edu.scientling.models.params;

/**
 * Created by Razjelll on 11.01.2017.
 */

public class WordsListParams {
    public enum Tabs {
        ALL(0),
        OWN(1),
        HARD(2);

        private final int id;

        Tabs(int id) {
            this.id = id;
        }

        public final int getValue() {
            return id;
        }
    }

    private long mSetId;
    private Tabs mTab;

    public long getSetId() {
        return mSetId;
    }

    public void setSetId(long setId) {
        mSetId = setId;
    }

    public Tabs getTab() {
        return mTab;
    }

    public void setTab(Tabs tab) {
        mTab = tab;
    }

    public void setTab(int tab) {
        switch (tab) {
            case 0:
                mTab = Tabs.ALL;
                break;
            case 1:
                mTab = Tabs.OWN;
                break;
            case 2:
                mTab = Tabs.HARD;
                break;
        }
    }
}
