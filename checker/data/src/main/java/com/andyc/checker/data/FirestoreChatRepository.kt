package com.andyc.checker.data

import android.util.Log
import com.andyc.checker.domain.Chat
import com.andyc.checker.domain.ChatRepository
import com.andyc.checker.domain.Message
import com.andyc.checker.domain.MessageRole
import com.andyc.core.domain.util.DataError
import com.andyc.core.domain.util.Result
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TAG = "FirestoreChatRepository"

class FirestoreChatRepository: ChatRepository {

    private val database = Firebase.firestore
    private val user = Firebase.auth.currentUser!!

    /**
     * Reference to the chat collection in Firestore
     */
    private val chatCollection = database.collection("users").document(user.uid)
        .collection("chats")

    private var chatHistory: List<Chat>? = null

    init {
        subscribeToChatUpdate()

//        val chat1 = Chat(
//            userId = user.uid,
//            title = "Do all dragons fly?",
//            messages = listOf(Message(
//                role = MessageRole.ASSISTANT,
//                sentEpochSecond = 1739298372,
//                content = "Most do, however, there are exceptions. What if a dragon is too heavy to fly?"
//            ))
//        )
//
//        val chat2 = Chat(
//            userId = user.uid,
//            title = "Are dogs descendants of wolves?",
//            messages = listOf(Message(
//                role = MessageRole.ASSISTANT,
//                sentEpochSecond = 1739398372,
//                content = "Yes, dogs as we know them are domesticated wolves"
//            ))
//        )
//
//        chatCollection.document(chat1.id)
//            .set(chat1)
//
//        chatCollection.document(chat2.id)
//            .set(chat2)
    }

    override suspend fun getChatHistory(): Result<List<Chat>, DataError> {
        chatHistory?.let {
            return Result.Success(it)
        }

        return Result.Error(DataError.Network.UNKNOWN)
    }

    private fun subscribeToChatUpdate() {
        chatCollection.addSnapshotListener { querySnapshot, e ->
            e?.let {
                Log.e(TAG, "Error getting chat history: ", e)
                chatHistory = null
                return@addSnapshotListener
            }

            querySnapshot?.let {
                Log.d(TAG, "Chat history got")
                val chats = it.map { document -> document.toObject<Chat>() }
                chatHistory = chats
                Log.d(TAG, chats.toString())
            }
        }
    }

    override suspend fun getChat(chatId: String): Result<Chat, DataError.Network> {
        var result: Result<Chat, DataError.Network> = Result.Error(DataError.Network.UNKNOWN)

        withContext(Dispatchers.IO) {
            chatCollection.document(chatId)
                .get()
                .addOnSuccessListener { document ->
                    Log.d(TAG, "Successfully got chat. ID: $chatId")
                    result = Result.Success(document.toObject<Chat>()!!)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error getting chat with ID $chatId: ", e)
                    result = Result.Error(DataError.Network.UNKNOWN)
                }
                .await()
        }

        return result
    }

    override suspend fun upsertChat(chat: Chat): Result<String, DataError.Network> {
        var result: Result<String, DataError.Network> = Result.Error(DataError.Network.UNKNOWN)

        withContext(Dispatchers.IO) {
            chatCollection.document(chat.id)
                .set(chat, SetOptions.merge())
                .addOnSuccessListener {
                    result = Result.Success(chat.id)
                    Log.d(TAG, "Successfully upserted chat. ID: ${chat.id}")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error inserting/updating chat: ", e)
                    result = Result.Error(DataError.Network.UNKNOWN)
                }
                .await()
        }

        return result
    }

    override suspend fun deleteChat(chatId: String): Result<String, DataError.Network> {
        var result: Result<String, DataError.Network> = Result.Error(DataError.Network.UNKNOWN)

        withContext(Dispatchers.IO) {
            chatCollection.document(chatId)
                .delete()
                .addOnSuccessListener {
                    result = Result.Success(chatId)
                    Log.d(TAG, "Successfully deleted chat. ID: $chatId")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error deleting chat with ID $chatId: ", e)
                    result = Result.Error(DataError.Network.UNKNOWN)
                }
                .await()
        }

        return result
    }
}