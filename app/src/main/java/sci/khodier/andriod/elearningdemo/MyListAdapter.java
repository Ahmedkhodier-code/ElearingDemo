package sci.khodier.andriod.elearningdemo;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private ArrayList<Course> listdata;
    static Bitmap bitmap = null;
    static Bitmap bt = null;

    // RecyclerView recyclerView;
    public MyListAdapter(ArrayList<Course> listdata, Context context) {
        this.listdata = listdata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Course myListData = listdata.get(position);
        holder.cousreName.setText(listdata.get(position).getName());
        holder.creator.setText("creator: "+listdata.get(position).getCreator());
        holder.setImageView(listdata.get(position).getImg());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "click on item: " + myListData.getName(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView cousreName , creator;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.img);
            this.cousreName = (TextView) itemView.findViewById(R.id.name);
            this.creator=(TextView) itemView.findViewById(R.id.creator);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }

        public void setImageView(String link) {
            LoadImage loadImage = new LoadImage(imageView);
            loadImage.execute(link);
        }
    }

    private static class LoadImage extends AsyncTask<String, Void, Bitmap> {

        ImageView imageView;

        public LoadImage(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            System.out.println("doInBackground");

            String urllink = strings[0];


            try {
                InputStream inputStream = new java.net.URL(urllink).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                System.out.println("try");
            } catch (IOException e) {
//                System.out.println("Error eccure");
                e.printStackTrace();
//                 url.setError("please");
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            bt = bitmap;
            imageView.setImageBitmap(bitmap);
        }
    }
}