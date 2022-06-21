package sci.khodier.andriod.elearningdemo;

import android.app.AlertDialog;
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

public class AnnTaskAdapterDegree extends RecyclerView.Adapter<AnnTaskAdapterDegree.ViewHolder> {
    private ArrayList<announcements> listdata;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String username = "";
    private static final String TAG = "commentRead";
    Context context;
    String role = "";
    DocumentReference ref;
    AnnTaskAdapterDegree.ViewHolder holder;
    announcements currentAnn;
    // RecyclerView recyclerView;

    public AnnTaskAdapterDegree(ArrayList<announcements> listdata, Context context) {
        this.listdata = listdata;
        this.context = context;
    }


    @Override
    public AnnTaskAdapterDegree.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item3_1, parent, false);
        getInfo();
        AnnTaskAdapterDegree.ViewHolder viewHolder = new AnnTaskAdapterDegree.ViewHolder(listItem);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(AnnTaskAdapterDegree.ViewHolder holder, int position) {
        this.holder = holder;
        final announcements currentAnn = listdata.get(position);
        this.currentAnn = currentAnn;
        int idx = position;
        holder.cousreName.setText(listdata.get(position).getCourseName());
        holder.message.setText("" + listdata.get(position).getMessage());
        holder.time.setText(listdata.get(position).getTime());
        String annId = listdata.get(position).getId();
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
                                System.out.println("Student");
                            } else {
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
                        Intent intent = new Intent(context, assActivityDegree.class);
                        intent.putExtra("courseName", currentAnn.getCourseName());
                        intent.putExtra("taskId", currentAnn.getId());
                        intent.putExtra("currentAnn", currentAnn);
                        intent.putExtra("annId", annId);
                        System.out.println("courseId00"+currentAnn.getCourseId());
                        intent.putExtra("courseId", currentAnn.getCourseId());

                        context.startActivity(intent);
                    }

                }
            });
        }
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

        public ViewHolder(View itemView) {
            super(itemView);
            this.cousreName = (TextView) itemView.findViewById(R.id.courseName);
            this.message = (TextView) itemView.findViewById(R.id.message);
            this.time = (TextView) itemView.findViewById(R.id.time);
            this.recyclerView = itemView.findViewById(R.id.comments);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }
}