package cn.jamesli.example.at81imageloader.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.jamesli.example.at81imageloader.Constants;
import cn.jamesli.example.at81imageloader.R;
import cn.jamesli.example.at81imageloader.activity.SimpleImageActivity;


// do not require scrolling, so do not need AbsListViewBaseFragment
public class ImageGalleryFragment extends BaseFragment {

    public static final int INDEX = 3;

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fr_image_gallery, container, false); // one simple gallery
        Gallery gallery = (Gallery) rootView.findViewById(R.id.gallery);
        gallery.setAdapter(new ImageAdapter(getActivity()));
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // show enlarged image in SimpleImageActivity
                startImagePagerActivity(position);
            }
        });
        return rootView;
    }


    private void startImagePagerActivity(int position) {
        Intent intent = new Intent(getActivity(), SimpleImageActivity.class);
        intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImagePagerFragment.INDEX);
        intent.putExtra(Constants.Extra.IMAGE_POSITION, position);
        startActivity(intent);
    }

    private static class ImageAdapter extends BaseAdapter {

        private static final String[] IMAGE_URLS = Constants.IMAGES;

        private LayoutInflater inflater;

        private DisplayImageOptions options;    // by Universal Image Loader

        ImageAdapter(Context context) {
            inflater = LayoutInflater.from(context);    // Obtains the LayoutInflater from the given context

            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_stub)
                    .showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.ic_error)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }

        @Override
        public int getCount() {
            return IMAGE_URLS.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = (ImageView) convertView;
            if (imageView == null) {
                // R.layout.item_gallery_image with one ImageView
                imageView = (ImageView) inflater.inflate(R.layout.item_gallery_image, parent, false);
            }
            ImageLoader.getInstance().displayImage(IMAGE_URLS[position], imageView, options);
            // attachToRoot: if false, root is only used to create the correct subclass of
            // LayoutParams for the root view in the XML.
            return imageView;
        }
    }
}
