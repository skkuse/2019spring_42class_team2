package com.example.photoblog.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.photoblog.Adapter.PostAdapter;
import com.example.photoblog.AddPostActivity;
import com.example.photoblog.JavaClass.BlogPost;
import com.example.photoblog.JavaClass.User;
import com.example.photoblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {


    private EditText SearchET;

    private FloatingActionButton floatingActionBtn;
    private RecyclerView recyclerViewId;
    private List<BlogPost> blogPostList;
    private List<User> userList;
    private FirebaseFirestore firebaseFirestore;
    private PostAdapter postAdapter;
    private FirebaseAuth firebaseAuth;
    private int pageLimit = 500;
    private Button Searchbtn;



    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        floatingActionBtn = rootView.findViewById(R.id.floatingActionBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerViewId = rootView.findViewById(R.id.recyclerViewId);
        blogPostList = new ArrayList<>();
        userList = new ArrayList<>();
        postAdapter = new PostAdapter(blogPostList, userList);
        recyclerViewId.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewId.setAdapter(postAdapter);

        // Inflate the layout for this fragment

        Searchbtn = rootView.findViewById(R.id.Searchbtn);
        SearchET = rootView.findViewById(R.id.SearchET);

        Searchbtn.setOnClickListener(mClickListener);

        return rootView;

    }

    Button.OnClickListener mClickListener = new View.OnClickListener(){
        public void onClick(View v)
        {


            /**
             * keyword가 검색어 keyword와 TAG를 비교해서 띄워줘야함
             */
            String keyword = "";
            if(SearchET.getText().toString().length() <=0)
                Toast.makeText(getActivity(), "검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
            else {
                keyword = SearchET.getText().toString();
            }

            Log.i("Keyword", keyword);

            if (firebaseAuth.getCurrentUser() != null) {

                firebaseFirestore = FirebaseFirestore.getInstance();

                recyclerViewId.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                        if (reachedBottom) {
                            Toast.makeText(getActivity(), "Next Page", Toast.LENGTH_SHORT).show();
                            loadNextPost();
                        }
                    }
                });


                Query firstQuery = firebaseFirestore.collection("Posts").orderBy("post_time", Query.Direction.DESCENDING).limit(pageLimit);

                firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (!documentSnapshots.isEmpty()) {

                            if (isFirstPageFirstLoad) {

                                lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                                blogPostList.clear();
                                userList.clear();

                            }

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                Log.i("????????", "??????");
                                if (doc.getType() == DocumentChange.Type.ADDED) {


                                    String blogPostId = doc.getDocument().getId();

                                    Log.i("blogpostid", blogPostId);
                                    final BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);////


                                    String blogUserId = doc.getDocument().getString("current_UserId");



                                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                                    Log.i("Current", currentUser.getUid());
                                    Log.i("bloguserid", blogUserId);


                                    /**
                                     *
                                     *
                                     * 이 아래 if continue 부분을 바꿔주면 됨
                                     */
                                    if (!blogUserId.equals(currentUser.getUid()))
                                        continue;


                                    firebaseFirestore.collection("Users").document(blogUserId).get()
                                            .addOnCompleteListener(getActivity(), new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {

                                                        User user = task.getResult().toObject(User.class);


                                                        Log.i("user", "onComplete: " + user);

                                                        if (isFirstPageFirstLoad) {

                                                            userList.add(user);
                                                            blogPostList.add(blogPost);

                                                            Log.i("User", user.getName());
                                                            Log.i("Blog", blogPost.getTitle());

                                                        } else {

                                                            Log.i("User2", user.getName());
                                                            Log.i("Blog2", blogPost.getTitle());

                                                            userList.add(0, user);
                                                            blogPostList.add(0, blogPost);
                                                        }
                                                        postAdapter.notifyDataSetChanged();
                                                        Log.i("!!!!!!!", "!!!!!!!!");
                                                    }
                                                }
                                            });
                                }
                            }
                            isFirstPageFirstLoad = false;
                        }//
                    }
                });
            }

            /*
            floatingActionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddPostActivity.class);
                    startActivity(intent);
                }
            });

*/

        }
    };


    public void loadNextPost() {

        Log.i("loadnext","post");
        if (firebaseAuth.getCurrentUser() != null) {

            Query nextQuery = firebaseFirestore.collection("Posts")
                    .orderBy("post_time", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(pageLimit);

            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                final BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);

                                String blogUserId = doc.getDocument().getString("current_UserId");

                                firebaseFirestore.collection("Users").document(blogUserId).get()
                                        .addOnCompleteListener(getActivity(), new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {

                                                    User user = task.getResult().toObject(User.class);

                                                    userList.add(user);
                                                    blogPostList.add(blogPost);


                                                    postAdapter.notifyDataSetChanged();

                                                }
                                            }
                                        });
                            }
                        }
                    }
                }
            });

        }

    }

}
