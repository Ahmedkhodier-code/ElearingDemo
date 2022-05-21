package sci.khodier.andriod.elearningdemo;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.Objects;


public class chatAdapter extends RecyclerView.Adapter<chatAdapter.ViewHolder> {
    private ArrayList<chat> listdata;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    private static final String TAG = "ReadAndWriteSnippets";
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    DocumentReference ref;
    String role = "";

    // RecyclerView recyclerView;
    public chatAdapter(ArrayList<chat> listdata, Context context) {
        this.listdata = listdata;
        this.context = context;
    }

    public String getRule() {
        ref = FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(currentUser.getEmail()));
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        role = doc.get("role") + "";
                    } else {
                        Log.d("Document", "No data");
                    }
                }
            }
        });
        System.out.println("the role is :" + role);
        return role;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm =   ((AppCompatActivity)context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = Objects.requireNonNull(fm).beginTransaction();
        //fragmentTransaction.replace(R.id.chatFrag, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item2, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final chat currentChat = listdata.get(position);
        holder.cousreName.setText(listdata.get(position).getCorseName());
        String courseId = listdata.get(position).getCourseId();
        int idx = position;

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message_frag message = new message_frag(context, currentUser ,courseId);
                Intent intent = new Intent (context ,chatActivity.class);
                intent.putExtra("courseId",courseId);
                context.startActivity(intent);
//                loadFragment(message);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView cousreName, time;
        public RelativeLayout relativeLayout;
        public Button del;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.deleteBtn).setVisibility(View.GONE);
            this.cousreName = (TextView) itemView.findViewById(R.id.name);
            itemView.findViewById(R.id.time).setVisibility(View.GONE);
            itemView.findViewById(R.id.creator).setVisibility(View.GONE);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

}