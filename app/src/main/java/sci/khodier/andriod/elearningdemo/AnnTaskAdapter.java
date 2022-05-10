package sci.khodier.andriod.elearningdemo;


import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

// 7oot l tasks fel course gowa be fragment lw7doo
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
    // RecyclerView recyclerView;

    public AnnTaskAdapter(ArrayList<announcements> listdata, Context context) {
        this.listdata = listdata;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item3, parent, false);
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
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //-----------------------------------------------------------
                //-------------------------------
            }
        });
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                addComment(holder.comment.getText().toString(),annId,currentDateandTime,username);
            }
        });
    }
    public void addComment(String text, String annId,String time,String userName) {
        final String TAG = "DocSnippets";
        // [START add_ada_lovelace]
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("username", userName);
        user.put("annId", annId);
        user.put("time", time);
        user.put("commentText", text);

        // Add a new document with a generated ID
        db.collection("announcements").document(annId).collection("comments").document().set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "comment added " + task.getResult());
                            System.out.println("comment added in db: " + task.getResult());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void getComments(RecyclerView recyclerView, String id ) {
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
        public RecyclerView recyclerView;
        ImageView commentBtn;
        EditText comment;
        public ViewHolder(View itemView) {
            super(itemView);
            this.comment=itemView.findViewById(R.id.comment);
            this.commentBtn=itemView.findViewById(R.id.commentBtn);
            this.cousreName = (TextView) itemView.findViewById(R.id.courseName);
            this.message = (TextView) itemView.findViewById(R.id.message);
            this.time = (TextView) itemView.findViewById(R.id.time);
            this.recyclerView = itemView.findViewById(R.id.comments);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }
}