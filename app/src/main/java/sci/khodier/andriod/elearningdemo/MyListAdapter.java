package sci.khodier.andriod.elearningdemo;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private ArrayList<Course> listdata;
    static Bitmap bitmap = null;
    static Bitmap bt = null;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "ReadAndWriteSnippets";
    Context context;
    DocumentReference ref;

    // RecyclerView recyclerView;

    public MyListAdapter(ArrayList<Course> listdata, Context context) {
        this.listdata = listdata;
        this.context = context;
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
        holder.creator.setText("creator: " + listdata.get(position).getCreator());
        holder.setImageView(listdata.get(position).getImg());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getRule().equals("Instructor")) {
                    Intent intent = new Intent(context , ActiveCourse.class);
                    intent.putExtra("","");
                    context.startActivity(intent);

                }
                Toast.makeText(view.getContext(), "click on item: " + myListData.getName(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public String getRule() {
        final String[] res = {""};
        ref = FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(currentUser.getEmail()));
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        res[0] ="" + doc.get("role");
                    } else {
                        Log.d("Document", "No data");
                    }
                }
            }
        });
        return res[0];
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView cousreName, creator;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.img);
            this.cousreName = (TextView) itemView.findViewById(R.id.name);
            this.creator = (TextView) itemView.findViewById(R.id.creator);
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