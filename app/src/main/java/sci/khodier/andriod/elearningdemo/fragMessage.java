package sci.khodier.andriod.elearningdemo;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class fragMessage extends Fragment {
    View rootView;
    ArrayList<chat> myListData = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "ReadAndWriteSnippets";
    FirebaseUser currentUser;
    Context context;

    public fragMessage(Context context, FirebaseUser currentUser) {
        this.context = context;
        this.currentUser = currentUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_message, container, false);
        getMaterial();
        return rootView;
    }

    public void getMaterial() {
        myListData = new ArrayList<>();
        System.out.println("from content");
        db.collection("users").document(currentUser.getEmail()).collection("courses")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                myListData.add(new chat(document.getString("courseId"),
                                        document.getString("courseName")));
                                System.out.println("-----------------------------------");
                            }
                            RecyclerView recyclerView = rootView.findViewById(R.id.chats);
                            chatAdapter adapter = new chatAdapter(myListData, getContext());
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            Toast.makeText(getContext(), "documents failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}