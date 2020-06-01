package com.dyszlewskiR.edu.scientling.presentation.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dyszlewskiR.edu.scientling.R;
import com.dyszlewskiR.edu.scientling.presentation.activity.LessonActivity;
import com.dyszlewskiR.edu.scientling.models.entity.Lesson;
import com.dyszlewskiR.edu.scientling.data.database.DataManager;
import com.dyszlewskiR.edu.scientling.utils.Constants;

import java.util.List;

/**
 * Created by Razjelll on 04.12.2016.
 */

public class LessonsAdapter extends BaseAdapter {
    private final int EDIT_REQUEST = 1221;

    private List<Lesson> mItems;
    private Context mContext;
    private int mResource;
    private boolean mIsSpinner;
    private int mLastEdited;
    private DataManager mDataManager;

    public LessonsAdapter(Context context, int resource, List<Lesson> data, DataManager dataManager) {
        //super(context, resource, data);
        mItems = data;
        mContext = context;
        mResource = resource;
        mDataManager = dataManager;
    }

    public LessonsAdapter(Context context, int resource, List<Lesson> data, boolean spinner) {
        mItems = data;
        mContext = context;
        mResource = resource;
        mIsSpinner = spinner;
    }

    public void setData(List<Lesson> data) {
        mItems = data;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Lesson getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(mResource, null);
            viewHolder = new ViewHolder(rowView);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        if (!mItems.get(position).getName().equals(Constants.DEFAULT_LESSON_NAME)) {
            viewHolder.lessonNameTextView.setText(mItems.get(position).getName());
        } else {
            viewHolder.lessonNameTextView.setText(mContext.getString(R.string.lack));
        }

        if (mItems.get(position).getNumber() != Constants.DEFAULT_LESSON_NUMBER) {
            viewHolder.lessonNumberTextView.setText(String.valueOf(mItems.get(position).getNumber()));
        } else {
            viewHolder.lessonNumberTextView.setText("");
        }
        if (!mIsSpinner && viewHolder.actionButton != null) {
            setupMenu(position, viewHolder);
            if (mItems.get(position).getNumber() == Constants.DEFAULT_LESSON_NUMBER) {
                viewHolder.actionButton.setVisibility(View.GONE);
            } else {
                //jest to potrzebne w przypadku, kiedy dodajemy nową lekcję
                //Bez tego menu jest niewidoczne
                viewHolder.actionButton.setVisibility(View.VISIBLE);
            }
        } else if (viewHolder.actionButton != null) {
            viewHolder.actionButton.setVisibility(View.GONE);
        }

        return rowView;
    }


    private void setupMenu(final int position, final ViewHolder viewHolder) {
        viewHolder.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, viewHolder.actionButton);
                popupMenu.getMenu().add(mContext.getString(Constants.MENU_EDIT));
                popupMenu.getMenu().add(mContext.getString(Constants.MENU_DELETE));

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals(mContext.getString(Constants.MENU_EDIT))) {
                            startEditLesson(position);
                        }
                        if (item.getTitle().equals(mContext.getString(Constants.MENU_DELETE))) {
                            assert mDataManager != null;
                            new DeleteLessonAlertDialog(mContext, mItems.get(position), mDataManager).show();
                        }

                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void startEditLesson(int position) {
        mLastEdited = position;
        Intent intent = new Intent(mContext, LessonActivity.class);
        intent.putExtra("item", mItems.get(position));
        intent.putExtra("edit", true);
        ((Activity) mContext).startActivityForResult(intent, EDIT_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Lesson lesson = data.getParcelableExtra("result");
                mItems.set(mLastEdited, lesson);
                notifyDataSetChanged();
            }
        }
    }

    private static class ViewHolder {
        public TextView lessonNameTextView;
        public TextView lessonNumberTextView;
        public ImageView actionButton;

        public ViewHolder(View view) {
            lessonNameTextView = (TextView) view.findViewById(R.id.lesson_text_view);
            lessonNumberTextView = (TextView) view.findViewById(R.id.lesson_number_text_view);
            actionButton = (ImageView) view.findViewById(R.id.action_button);
        }

    }

    private class DeleteLessonAlertDialog extends AlertDialog {

        public DeleteLessonAlertDialog(Context context, final Lesson lesson, final DataManager dataManager) {
            super(context);
            setTitle(context.getString(R.string.deleting_lesson));
            int wordCount = dataManager.getWordsCountInLesson(lesson.getId());
            String message = context.getString(R.string.sure_delete_category) + "\n"
                    + lesson.getNumber() + " " + lesson.getName();
            String positiveButtonText;
            if (wordCount != 0) {
                positiveButtonText = mContext.getString(R.string.delete_with_words);
                message += "\n" + mContext.getString(R.string.word_lesson_connection) + " " + wordCount;
                setButton(BUTTON_NEUTRAL, context.getString(R.string.delete_without_words), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataManager.deleteLesson(lesson, -1);
                        mItems.remove(lesson);
                        notifyDataSetChanged();
                    }
                });
            } else { //wordCount == 0
                positiveButtonText = mContext.getString(R.string.yes);
            }
            setButton(BUTTON_POSITIVE, positiveButtonText, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dataManager.deleteLesson(lesson, -1);
                    mItems.remove(lesson);
                    notifyDataSetChanged();
                }
            });
            setButton(BUTTON_NEGATIVE, context.getString(R.string.cancel), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            setMessage(message);
        }
    }
}
