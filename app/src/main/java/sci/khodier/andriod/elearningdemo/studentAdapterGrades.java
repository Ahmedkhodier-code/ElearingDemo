package sci.khodier.andriod.elearningdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class studentAdapterGrades extends RecyclerView.Adapter<studentAdapter.ViewHolder> {
    private ArrayList<student> listdata;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    private static final String TAG = "ReadAndWriteSnippets";
    Context context;
    String role = "";
    DocumentReference ref;

    // RecyclerView recyclerView;
    public studentAdapterGrades(ArrayList<student> listdata, Context context) {
        this.listdata = listdata;
        this.context = context;
    }


    @Override
    public studentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item6, parent, false);
        studentAdapter.ViewHolder viewHolder = new studentAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(studentAdapter.ViewHolder holder, int position) {
        final student currentCourse = listdata.get(position);
        holder.Name.setText(listdata.get(position).getName());
        String name = listdata.get(position).getName();
        holder.Email.setText(listdata.get(position).getEmail());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "clicked on student" + name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView Name, Email;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.Name = (TextView) itemView.findViewById(R.id.name);
            this.Email = itemView.findViewById(R.id.creator);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }
}
