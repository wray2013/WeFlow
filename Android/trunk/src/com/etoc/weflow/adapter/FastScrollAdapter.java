package com.etoc.weflow.adapter;

import android.content.Context;
import android.widget.SectionIndexer;


public class FastScrollAdapter extends SimpleAdapter implements SectionIndexer {

    private SimpleItem[] sections;

    public FastScrollAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    @Override 
    protected void prepareSections(int sectionsNumber) {
        sections = new SimpleItem[sectionsNumber];
    }

    @Override 
    protected void onSectionAdded(SimpleItem section, int sectionPosition) {
        sections[sectionPosition] = section;
    }

    @Override 
    public SimpleItem[] getSections() {
        return sections;
    }

    @Override public int getPositionForSection(int section) {
        if (section >= sections.length) {
            section = sections.length - 1;
        }
        return sections[section].listPosition;
    }

    @Override 
    public int getSectionForPosition(int position) {
        if (position >= getCount()) {
            position = getCount() - 1;
        }
        return ((SimpleItem)getItem(position)).sectionPosition;
    }
    
}
