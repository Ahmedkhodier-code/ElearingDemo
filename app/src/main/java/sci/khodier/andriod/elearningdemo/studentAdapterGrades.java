package sci.khodier.andriod.elearningdemo;

import android.content.Context;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class studentAdapterGrades extends RecyclerView.Adapter<studentAdapterGrades.ViewHolder> {
    private ArrayList<student> listdata;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    private static final String TAG = "ReadAndWriteSnippets";
    Context context;
    boolean f = true;
    String annId;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    // RecyclerView recyclerView;
    public studentAdapterGrades(ArrayList<student> listdata, Context context , String annId) {
        this.listdata = listdata;
        this.context = context;
        this.annId=annId;
    }


    @Override
    public studentAdapterGrades.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item6, parent, false);
        studentAdapterGrades.ViewHolder viewHolder = new studentAdapterGrades.ViewHolder(listItem);
        return viewHolder;
    }

    private void Toggle(LinearLayout Lin, boolean flag) {
        if (flag) {
            Transition transition = new Slide(Gravity.LEFT);
            transition.setDuration(700);
            transition.addTarget(R.id.linDegree);
            TransitionManager.beginDelayedTransition(Lin, transition);
            Lin.setVisibility(View.VISIBLE);
        } else {
            Transition transition = new Slide(Gravity.RIGHT);
            transition.setDuration(300);
            transition.addTarget(R.id.linDegree);
            TransitionManager.beginDelayedTransition(Lin, transition);
            Lin.setVisibility(View.GONE);
        }
        f = !flag;
    }

    public void addDegree(String Email, String userName, String degree, String taskId) {
        final String TAG = "DocSnippets";
        // [START add_ada_lovelace]
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("name", userName);
        user.put("StudentEmail", Email);
        user.put("degree", degree);

        // Add a new document with a generated ID
        db.collection("tasks").document(taskId).collection("degree").document(Email).set(user)
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

    @Override
    public void onBindViewHolder(studentAdapterGrades.ViewHolder holder, int position) {
        final student currentStudent = listdata.get(position);
        String name = listdata.get(position).getName();
        String Email=listdata.get(position).getEmail();
        String degree=listdata.get(position).getDegree();
        System.out.println("name:" +name +" Email: " +Email+" degree: "+degree);
        holder.Name.setText(listdata.get(position).getName());
        holder.Email.setText(listdata.get(position).getEmail());
        holder.degree.setText(listdata.get(position).getDegree());
        System.out.println();
        holder.saveDegree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deg = holder.editDegree.getText().toString();
                if (deg.equals("") || deg != null || deg != "" || !deg.isEmpty()) {
                    addDegree(Email,name,deg,annId);
                }
                holder.editDegree.setText("");
                Toggle(holder.linDegree, false);

            }
        });
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "clicked on  " + name, Toast.LENGTH_SHORT).show();
                Toggle(holder.linDegree, f);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView Name, Email, degree, editDegree;
        public RelativeLayout relativeLayout;
        public LinearLayout linDegree;
        public Button saveDegree;

        public ViewHolder(View itemView) {
            super(itemView);
            this.linDegree = itemView.findViewById(R.id.linDegree);
            this.Name = (TextView) itemView.findViewById(R.id.name);
            this.Email = itemView.findViewById(R.id.creator);
            this.editDegree = itemView.findViewById(R.id.degreeNum);
            this.degree = itemView.findViewById(R.id.degree);
            this.saveDegree = itemView.findViewById(R.id.saveDegree);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }
}
