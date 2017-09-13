package ptit.nttrung.chatusefirebase.base;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by TrungNguyen on 9/12/2017.
 */

public abstract class BaseFirebase {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    /**
     * Dành cho database
     * @return
     */
    protected DatabaseReference getDatabaseReference() {
        if (mDatabase == null)
            return  FirebaseDatabase.getInstance().getReference();
        else return mDatabase;
    }

    /**
     * Dành cho Auth
     * @return
     */
    protected FirebaseAuth getFirebaseAuth() {
        if (mAuth == null)
            return  FirebaseAuth.getInstance();
        else return mAuth;
    }

    /**
     * Dành cho upload file
     * @return
     */
    protected StorageReference getStorageReference() {
        if (mStorageRef == null)
            return  FirebaseStorage.getInstance().getReference();
        else return mStorageRef;
    }
}
