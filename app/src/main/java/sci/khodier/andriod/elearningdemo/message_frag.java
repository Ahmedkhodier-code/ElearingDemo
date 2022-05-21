package sci.khodier.andriod.elearningdemo;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;


public class message_frag extends Fragment {
    Context context;
    FirebaseUser currentUser;
    ProgressBar progressBar;
    RecyclerView messageRecyclerView;
    ImageView addMessageImageView;
    ImageView profilepic;
    String userName = "", profileImg = null;
    DocumentReference ref;
    private static final String TAG = "MainActivity";
    String courseId="";
    public static final String MESSAGES_CHILD = "messages";
    public static final String ANONYMOUS = "anonymous";
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    private static final int REQUEST_IMAGE = 2;
    Button sendButton;
    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    EditText messageEditText;

    public message_frag(Context context, FirebaseUser currentUser , String courseId) {
        this.context = context;
        this.currentUser = currentUser;
        this.courseId=courseId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_message, container, false);
        progressBar = rootView.findViewById(R.id.progressBar);
        messageRecyclerView = rootView.findViewById(R.id.messageRecyclerView);
        // Inflate with ViewBinding
        // Set the root view from ViewBinding instance
        messageEditText = rootView.findViewById(R.id.messageEditText);
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        sendButton = rootView.findViewById(R.id.sendButton);
        addMessageImageView = rootView.findViewById(R.id.addMessageImageView);
        ref = FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(currentUser.getEmail()));
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        userName = doc.get("username") + "";
                        profileImg = doc.get("profImage") + "";

                    } else {
                        Log.d("Document", "No data");
                    }
                }
            }
        });
        // Initialize Realtime Database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        // Get the reference to the "messages" child node to be observed for changes
        DatabaseReference messagesRef = mFirebaseDatabase.getReference().child(MESSAGES_CHILD).child(courseId);

        // Configure the options required for FirebaseRecyclerAdapter with the above Query reference
        FirebaseRecyclerOptions<FriendlyMessage> options = new FirebaseRecyclerOptions.Builder<FriendlyMessage>()
                .setQuery(messagesRef, FriendlyMessage.class)
                // Listen to the changes in the Query and automatically update to the UI
                .setLifecycleOwner(this)
                .build();

        // Construct the FirebaseRecyclerAdapter with the options set
        FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(options) {
            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MessageViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_message, parent, false)
                );
            }

            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull FriendlyMessage message) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                holder.bindMessage(message);
            }
        };

        // Initialize LinearLayoutManager and RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(linearLayoutManager);
        messageRecyclerView.setAdapter(firebaseRecyclerAdapter);
        messageRecyclerView.addItemDecoration(new VerticalListItemSpacingDecoration(
                getResources().getDimensionPixelSize(R.dimen.main_item_list_spacing),
                getResources().getDimensionPixelSize(R.dimen.main_item_parent_spacing)
        ));

        // Register an observer for watching changes in the Adapter data in order to scroll
        // to the bottom of the list when the user is at the bottom of the list
        // in order to show newly added messages
        firebaseRecyclerAdapter.registerAdapterDataObserver(
                new ScrollToBottomObserver(
                        messageRecyclerView,
                        firebaseRecyclerAdapter,
                        linearLayoutManager
                )
        );

        // Disable the send button when there is no text in this input message field
        messageEditText.addTextChangedListener(new ButtonObserver(sendButton));

        // Register a click listener on the Send Button to send messages on click
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendlyMessage friendlyMessage = new FriendlyMessage(
                        getMessageToSend(),
                        getUserName(),
                        getUserPhotoUrl(),
                        null /* not an image based message */
                );

                // Create a child reference and set the user's message at that location
                mFirebaseDatabase.getReference().child(MESSAGES_CHILD).child(courseId)
                        .push().setValue(friendlyMessage);
                // Clear the input message field for the next message
                messageEditText.setText("");
            }
        });

        // Register a click listener on the Add Image Button to send messages with Image on click
        addMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch Gallery Intent for Image selection
                IntentUtility.launchGallery(getActivity(), REQUEST_IMAGE);
            }
        });
        return rootView;
    }

    /**
     * Returns the URL to the User's profile picture as stored in Firebase Project's user database.
     * Can be {@code null} when not present or if user is not authenticated.
     */
    @Nullable
    private String getUserPhotoUrl() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null ) {
            System.out.println("profileImg " + profileImg);
            return profileImg;
        }
        return null;
    }

    /**
     * Returns the display name of the User as stored in Firebase Project's user database.
     * Can be {@link this.ANONYMOUS} if the user is not authenticated.
     */
    private String getUserName() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            System.out.println("userName "+userName);
            return userName;
        }
        return ANONYMOUS;
    }

    /**
     * Extracts the user typed message from 'R.id.messageEditText' EditText and returns the same.
     * Can be an empty string when there is no message typed in.
     */
    private String getMessageToSend() {
        if (messageEditText.getText() == null) {
            return "";
        } else {
            return messageEditText.getText().toString();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            // If the request was for Image selection

            if (resultCode == RESULT_OK && data != null) {
                // If we have the result and its data

                // Get the URI to the image file selected
                final Uri imageUri = data.getData();

                // Construct a message with temporary loading image
                final FriendlyMessage tempMessage = new FriendlyMessage(
                        getMessageToSend(),  // If user has entered some message, publish it as well
                        getUserName(),
                        getUserPhotoUrl(),
                        LOADING_IMAGE_URL  // Temporary image with loading indicator
                );

                // Create a child reference and set the user's message at that location
                mFirebaseDatabase.getReference().child(MESSAGES_CHILD)
                        .push().setValue(tempMessage, new DatabaseReference.CompletionListener() {

                    @Override
                    public void onComplete(
                            @Nullable DatabaseError error,
                            @NonNull DatabaseReference ref) {
                        // Check the error
                        if (error != null) {
                            // Log the error and return
                            Log.w(TAG,
                                    "Unable to write message to the database.",
                                    error.toException()
                            );
                            return;
                        }

                        // Get the key to this database reference
                        String databaseKey = ref.getKey();

                        // Create a StorageReference for the Image to be uploaded
                        // in the hierarchy of the database key reference
                        //noinspection ConstantConditions
                        StorageReference storageReference = FirebaseStorage.getInstance()
                                // Create a child location for the current user
                                .getReference(mFirebaseAuth.getCurrentUser().getUid())
                                // Create a child location for the database key
                                .child(databaseKey)
                                // Create a child with the filename
                                .child(imageUri.getLastPathSegment());

                        // Begin upload of selected image
                        putImageInStorage(storageReference, imageUri, databaseKey, tempMessage);

                        // Clear the input message field if any for the next message
                        messageEditText.setText("");
                    }
                });

            }
        }
    }

    private void putImageInStorage(final StorageReference storageReference,
                                   final Uri imageUri,
                                   final String databaseKey,
                                   final FriendlyMessage tempMessage) {
        // Upload the selected image
        UploadTask uploadTask = storageReference.putFile(imageUri);

        // Chain UploadTask to get the resulting URI Task of the uploaded image
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                // Return the resulting URI Task of the uploaded image
                //noinspection ConstantConditions
                return task.getResult().getStorage().getDownloadUrl();
            }
        }).addOnSuccessListener(getActivity(), new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // When all tasks have completed successfully, update the corresponding reference
                // in the database with the URI of the uploaded image
                tempMessage.setImageUrl(uri.toString());
                mFirebaseDatabase.getReference().child(MESSAGES_CHILD)
                        .child(databaseKey)
                        .setValue(tempMessage);
            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Log the exception in case of failure
                Log.w(TAG, "Image upload task was not successful.", e);
            }
        });
    }
}

