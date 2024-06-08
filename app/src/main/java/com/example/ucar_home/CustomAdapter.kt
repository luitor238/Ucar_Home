package com.example.ucar_home


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CarAdapter(private val carList: List<CarObject>) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    inner class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val carImageView: ImageView = itemView.findViewById(R.id.imageView)
        val carNameTextView: TextView = itemView.findViewById(R.id.nombreUsuario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_car, parent, false)
        return CarViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val currentCar = carList[position]
        holder.carNameTextView.text = currentCar.title
        Glide.with(holder.itemView.context).load(currentCar.imageUrl).into(holder.carImageView)
    }

    override fun getItemCount(): Int {
        return carList.size
    }
}


class SearchAdapter(private val postList: List<PostObject>) : RecyclerView.Adapter<SearchAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentPost = postList[position]
        Glide.with(holder.itemView.context).load(currentPost.imageUrl).into(holder.postImageView)
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}


class PostAdapter(private var postsList: MutableMap<PostObject, User>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postsList.keys.elementAt(position)
        val user = postsList[post]

        holder.bind(post, user)
    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    fun updatePosts(newPostsList: MutableMap<PostObject, User>) {
        postsList.clear()
        postsList.putAll(newPostsList)
        notifyDataSetChanged()
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userProfileImage: ImageButton = itemView.findViewById(R.id.btnPerfil)
        private val userName: TextView = itemView.findViewById(R.id.nombreUsuario)
        private val postImage: ImageView = itemView.findViewById(R.id.imageView)
        private val postDescription: TextView = itemView.findViewById(R.id.textView3)

        fun bind(post: PostObject, user: User?) {
            user?.let {
                userName.text = it.name

                // Cargar la imagen del perfil del usuario
                if (it.imageUrl.isNotEmpty()) {
                    val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(it.imageUrl)
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        Glide.with(userProfileImage.context)
                            .load(uri)
                            .into(userProfileImage)
                    }
                } else {
                    userProfileImage.setImageResource(R.drawable.image_photo) // Imagen por defecto si no hay URL
                }
            }

            postDescription.text = post.description

            // Cargar la imagen de la publicación
            if (post.imageUrl.isNotEmpty()) {
                val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(post.imageUrl)
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(postImage.context)
                        .load(uri)
                        .into(postImage)
                }
            } else {
                postImage.setImageResource(R.drawable.image_photo) // Imagen por defecto si no hay URL
            }
        }
    }
}

class ChatProfileAdapter(private var userList: List<Chat>) : RecyclerView.Adapter<ChatProfileAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profilePic: ImageView = view.findViewById(R.id.chat_profile_pic)
        val userName: TextView = view.findViewById(R.id.chat_name)
        val lastMessage: TextView = view.findViewById(R.id.chat_last_message)
        val messageTime: TextView = view.findViewById(R.id.chat_time)
        val messageCount: TextView = view.findViewById(R.id.message_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        Glide.with(holder.profilePic.context).load(user.imageUrl).into(holder.profilePic)
        holder.userName.text = user.username
        holder.lastMessage.text = user.lastMessage
        holder.messageTime.text = user.timestamp.toString()
        holder.messageCount.text = user.unreadMessages
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateList(filteredList: List<Chat>) {
        userList = filteredList
        notifyDataSetChanged()
    }
}

class UserProfileAdapter(private var userList: List<User>, private val onItemClickListener: (User) -> Unit) : RecyclerView.Adapter<UserProfileAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profilePic: ImageView = view.findViewById(R.id.chat_profile_pic)
        val userName: TextView = view.findViewById(R.id.chat_name)
        val lastMessage: TextView = view.findViewById(R.id.chat_last_message)
        val messageTime: TextView = view.findViewById(R.id.chat_time)
        val messageCount: TextView = view.findViewById(R.id.message_count)

        fun bind(user: User, onItemClickListener: (User) -> Unit) {
            Glide.with(profilePic.context).load(user.imageUrl).into(profilePic)
            userName.text = user.username
            lastMessage.text = user.bibliography

            itemView.setOnClickListener {
                onItemClickListener(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateList(filteredList: List<User>) {
        userList = filteredList
        notifyDataSetChanged()
    }
}
