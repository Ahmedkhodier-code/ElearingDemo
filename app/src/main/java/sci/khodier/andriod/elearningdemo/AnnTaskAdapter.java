package sci.khodier.andriod.elearningdemo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class AnnTaskAdapter extends RecyclerView.Adapter<AnnTaskAdapter.ViewHolder> {
    private ArrayList<comment> listComment;
    private ArrayList<announcements> listdata;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String username = "";
    private static final String TAG = "commentRead";
    Context context;
    String role = "";
    DocumentReference ref;
    Fragment frag;
    // RecyclerView recyclerView;

    public AnnTaskAdapter(ArrayList<announcements> listdata, Context context) {
        this.listdata = listdata;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item5, parent, false);
        getInfo();
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final announcements currentAnn = listdata.get(position);
        listComment = new ArrayList<>();
        holder.cousreName.setText(listdata.get(position).getCourseName());
        holder.message.setText("" + listdata.get(position).getMessage());
        holder.time.setText(listdata.get(position).getTime());
        String annId = listdata.get(position).getId();
        getComments(holder.recyclerView, annId);
        holder.uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("application/*");
                frag.startActivityForResult(galleryIntent, 1);
            }
        });


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //-----------------------------------------------------------
                //-------------------------------
            }
        });
    }

    public void getComments(RecyclerView recyclerView, String id) {
        listComment = new ArrayList<comment>();
        db.collection("announcements").document(id).collection("comments")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        listComment.add(new comment(document.getString("commentText"), document.get("time") + "",
                                document.getString("username")));
                        System.out.println("-------------------/////----------------");
                    }
                    commentAdapter adapter = new commentAdapter(listComment, context);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(adapter);
                    listComment = new ArrayList<comment>();

                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                    Toast.makeText(context, "Student failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getInfo() {
        ref = FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(currentUser.getEmail()));
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        username = ("" + doc.get("username"));
                        role = ("" + doc.get("role"));
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView cousreName, message, time;
        public RelativeLayout relativeLayout;
        public ImageView uploadBtn;
        public RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.cousreName = (TextView) itemView.findViewById(R.id.courseName);
            this.message = (TextView) itemView.findViewById(R.id.message);
            this.time = (TextView) itemView.findViewById(R.id.time);
            this.uploadBtn = itemView.findViewById(R.id.uploadBtn);
            this.recyclerView = itemView.findViewById(R.id.comments);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }
}