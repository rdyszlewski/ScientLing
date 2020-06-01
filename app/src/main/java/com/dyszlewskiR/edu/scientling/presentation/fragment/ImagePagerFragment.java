package com.dyszlewskiR.edu.scientling.presentation.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dyszlewskiR.edu.scientling.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImagePagerFragment extends Fragment {

    private Uri mUri;
    private ImageView mImageView;


    public ImagePagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        mUri = bundle.getParcelable("image");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_pager, container, false);
        mImageView = (ImageView) view.findViewById(R.id.image_image_view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setImage();
    }

    private void setImage() {
        mImageView.setImageURI(mUri);
    }

}
