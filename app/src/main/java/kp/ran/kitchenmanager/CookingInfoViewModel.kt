package kp.ran.kitchenmanager

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
 import kotlinx.coroutines.tasks.await

class CookingInfoViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val collection = db.collection("cooking_info")
    val messagesCollection = db.collection("messages")

    fun saveCookingInfo(cookingInfo: CookingInfo) {
        // You can use `currentUserId` here to associate the cooking info with the current user
     //   if (currentUserId != null) {
            // Add the user ID to the cooking info
      //      cookingInfo.userId = currentUserId
            // Save the cooking info in Firestore
            collection.add(cookingInfo)
     //   } else {
            // Handle the case when the user is not authenticated
     //   }
    }

    fun getCookingInfoList(): Flow<List<CookingInfo>> = flow {

        val snapshot = collection.get().await()

        val cookingInfoList = snapshot.toObjects(CookingInfo::class.java)

        emit(cookingInfoList)

    }
    fun sendmessage(message: MessageFeature) :Boolean{

        var statu by mutableStateOf(false)

        messagesCollection
            .document("${message.currentUserid} - ${message.currentUserid}")
            .collection("user_messages")
            .add(message)
            .addOnSuccessListener {
                statu = true
            }
            .addOnFailureListener {
                statu = false
            }

        /*   messagesCollection
               .document("${message.currentUserid}-$message.cookUserId")
               .collection("user_messages")
               .orderBy("timestamp")
               .addSnapshotListener { snapshot, e ->
                   if (e != null) {
                       // Handle the error
                       return@addSnapshotListener
                   }

                   if (snapshot != null) {
                       // Process the messages in the snapshot
                       //  newmessage= snapshot.toObjects(MessageFeature::class.java)
                       // Update your UI to display the messages
                   }
               }*/

        return  statu
    }

}
