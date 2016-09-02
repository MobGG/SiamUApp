package siamumap.adaptor;

/**
 * Created by Mob on 11-Oct-15.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.siam.siamumap.AppMethod;
import edu.siam.siamumap.R;
import siamumap.dto.Post;

public class MissingItemCustomAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Post> posts;
    Post post;
    ViewHolder myViewHolder;

    private static class ViewHolder {
        ImageView image;
        TextView id, title, place, dateTime, loading;
        int position;
    }

    public MissingItemCustomAdapter(Context context, ArrayList<Post> posts) {
        this.mContext = context;
        this.posts = posts;
    }

    public int getCount() {
        return posts.size();
    }

    public Object getItem(int position) {
        return posts.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.missing_custom_listview, parent, false);
            myViewHolder = new ViewHolder();
            myViewHolder.id = (TextView) convertView.findViewById(R.id.post_id);
            myViewHolder.title = (TextView) convertView.findViewById(R.id.post_title);
            myViewHolder.place = (TextView) convertView.findViewById(R.id.post_place);
            myViewHolder.dateTime = (TextView) convertView.findViewById(R.id.post_date);
            myViewHolder.image = (ImageView) convertView.findViewById(R.id.post_thumbnail);
            myViewHolder.loading = (TextView) convertView.findViewById(R.id.loading);
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (ViewHolder) convertView.getTag();
        }
        AppMethod appMethod = new AppMethod();

        myViewHolder.position = position;
        post = posts.get(position);

        new decodeTask(position, myViewHolder, post).execute();
        myViewHolder.id.setText(post.getPostID());
        myViewHolder.title.setText(post.getPostTitle());
        myViewHolder.title.setTextColor(Color.parseColor("#000000"));
        myViewHolder.place.setText("อาคาร " + String.valueOf(post.getPostPlace()));
        myViewHolder.place.setTextColor(Color.parseColor("#000000"));
        myViewHolder.dateTime.setText(post.getPostDate());
        myViewHolder.dateTime.setTextColor(Color.parseColor("#000000"));

        return convertView;
    }

    private static class decodeTask extends AsyncTask<Void, Void, Bitmap> {
        AppMethod appMethod = new AppMethod();
        private int mPosition;
        private ViewHolder mHolder;
        private Post mPost;

        public decodeTask(int position, ViewHolder holder, Post post) {
            mPosition = position;
            mHolder = holder;
            mPost = post;
        }

        @Override
        protected void onPreExecute() {
            mHolder.image.setVisibility(View.INVISIBLE);
            mHolder.loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(Void... arg0) {
            if (mPost.getPostImage() != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                options.inPurgeable = true;
                byte[] decodedString = Base64.decode(mPost.getPostImage(), Base64.DEFAULT);
                options.inSampleSize = appMethod.calculateInSampleSize(options, 20, 20);
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
                return bitmap;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mHolder.position == mPosition && bitmap != null) {
                mHolder.image.setImageBitmap(bitmap);
            } else if (mHolder.position == mPosition && bitmap == null) {
                mHolder.image.setImageResource(R.drawable.no_picture);
            }
            mHolder.loading.setVisibility(View.INVISIBLE);
            mHolder.image.setVisibility(View.VISIBLE);
        }
    }
}