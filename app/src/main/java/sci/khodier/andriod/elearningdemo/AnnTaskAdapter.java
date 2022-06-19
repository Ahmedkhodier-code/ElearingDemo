package sci.khodier.andriod.elearningdemo;


import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    ViewHolder holder;
    announcements currentAnn;
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
        this.holder=holder;
        final announcements currentAnn = listdata.get(position);
        this.currentAnn=currentAnn;
        listComment = new ArrayList<>();
        int idx = position;
        holder.cousreName.setText(listdata.get(position).getCourseName());
        holder.message.setText("" + listdata.get(position).getMessage());
        holder.time.setText(listdata.get(position).getTime());
        String annId = listdata.get(position).getId();
        getComments(holder.recyclerView, annId, currentAnn.getType());
        if (currentAnn.getType().equals("tasks") || currentAnn.getType() == "tasks") {

            ref = FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(currentUser.getEmail()));
            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            role = ("" + doc.get("role"));
                            if (role.equals("Student") || role == "Student") {
                                holder.del.setVisibility(View.GONE);
                            } else {
                                holder.del.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.d("Document", "No data");
                        }
                    }
                }
            });
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "THE TYPE OF THIS IS " + currentAnn.getType(), Toast.LENGTH_SHORT).show();
                    if (currentAnn.getType().equals("tasks") || currentAnn.getType() == "tasks") {
                        Intent intent = new Intent(context, assActivity.class);
                        intent.putExtra("courseName",currentAnn.getCourseName());
                        intent.putExtra("taskId",currentAnn.getId());
                        intent.putExtra("currentAnn",currentAnn);
                        intent.putExtra("annId",annId);
                        intent.putExtra("courseId",currentAnn.getCourseId());

                        context.startActivity(intent);
                    }

                }
            });
        }
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog)
                        .setTitle("Delete File")
                        .setMessage("Are you sure, you want to delete this file?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseFirestore.getInstance().collection(currentAnn.getType())
                                        .document(annId).delete().
                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    System.out.println("courseId: " + annId);
                                                    listdata.remove(idx);
                                                    Toast.makeText(context, "Deleted Successfully",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(context, "Failed to delete",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }

                        })
                        .setNegativeButton("Cancel", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.comment.getText().toString().isEmpty()||holder.comment.getText().toString().equals(null)||
                        holder.comment.getText().toString().equals("")){
                    Toast.makeText(context, "please Enter comment first", Toast.LENGTH_SHORT).show();

                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                addComment(holder.comment.getText().toString(), annId, currentDateandTime, username, currentAnn.getType());
                holder.comment.setText("");
                getComments(holder.recyclerView, annId, currentAnn.getType());
            }
        });
    }

    public void addComment(String text, String annId, String time, String userName, String type) {
        final String TAG = "DocSnippets";
        // [START add_ada_lovelace]
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("username", userName);
        user.put("annId", annId);
        user.put("time", time);
        user.put("commentText", text);

        // Add a new document with a generated ID
        db.collection(type).document(annId).collection("comments").document().set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "comment added " + task.getResult());
                            System.out.println("comment added in db: " + task.getResult());
                            holder.comment.setText("");
                            getComments(holder.recyclerView, annId, currentAnn.getType());
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

    public void getComments(RecyclerView recyclerView, String id, String type) {
        listComment = new ArrayList<comment>();
        db.collection(type).document(id).collection("comments")
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
        public Button del;

        public ViewHolder(View itemView) {
            super(itemView);
            this.del = itemView.findViewById(R.id.deleteBtn);
            this.comment = itemView.findViewById(R.id.comment);
            this.commentBtn = itemView.findViewById(R.id.commentBtn);
            this.cousreName = (TextView) itemView.findViewById(R.id.courseName);
            this.message = (TextView) itemView.findViewById(R.id.message);
            this.time = (TextView) itemView.findViewById(R.id.time);
            this.recyclerView = itemView.findViewById(R.id.comments);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }
}