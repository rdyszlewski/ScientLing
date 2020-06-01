package com.dyszlewskiR.edu.scientling.presentation.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.models.entity.Language;

import java.util.List;

public class LanguagesAdapter extends ArrayAdapter {

    private final String RESOURCE_TYPE = "string";


    private List<Language> mItems;
    private Context mContext;
    private int mResource;

    public LanguagesAdapter(Context context, int resource, List<Language> data) {
        super(context, resource, data);
        mContext = context;
        mResource = resource;
        mItems = data;
    }

    public int getPosition(String name) {
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getName().equals(name)) {
                return i;
            }
        }
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, null);
        }
        int stringIdentifier = mContext.getResources().getIdentifier(mItems.get(position).getName(), RESOURCE_TYPE, mContext.getPackageName());
        TextView languageName = (TextView) convertView.findViewById(R.id.language_name);
        languageName.setText(mContext.getResources().getString(stringIdentifier));

        return convertView;
    }

    /**
     * Metoda konwertująca dane na widok widoczny podczas rozwijania elementu Spinner.
     * Języki znajdujące się w bazie zostają tłumaczone przez uzyskanie odpowiedniej wartości
     * z plików string.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, null);
        }
        int stringIdentifier = mContext.getResources().getIdentifier(mItems.get(position).getName(), RESOURCE_TYPE, mContext.getPackageName());
        TextView languageName = (TextView) convertView.findViewById(R.id.language_name);
        languageName.setText(mContext.getResources().getString(stringIdentifier));

        return convertView;
    }


}
