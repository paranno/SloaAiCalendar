package com.cookandroid.sloaapp.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.sloaapp.navigation.CommentActivity
import com.cookandroid.sloa.navigation.model.ContentDTO
import com.example.sloaapp.R
import com.example.sloaapp.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.fragment_user.view.*


class UserFragment : Fragment(){
    var fragmentView : View? = null
    var firestore : FirebaseFirestore? = null
    var uid :String? = null
    var auth : FirebaseAuth?= null
    var currentUserUid : String? = null
    companion object {
        var PICK_PROFILE_FROM_ALBUM = 10
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_user,container,false)
        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserUid = auth?.currentUser?.uid

        if(uid == currentUserUid){
            //MyPage
            fragmentView?.account_btn_follow_signout?.text = getString(R.string.signout)
            fragmentView?.account_btn_follow_signout?.setOnClickListener {
                activity?.finish()
                startActivity(Intent(activity, SignInActivity::class.java))
                auth?.signOut()
            }
        }else{
            //OtherUserPage
            fragmentView?.account_btn_follow_signout?.text = getString(R.string.follow)
            var mainactivity = (activity as CommentActivity)


        }

        fragmentView?.account_reyclerview?.adapter = UserFragmentRecyclerViewAdapter()
        fragmentView?.account_reyclerview?.layoutManager = GridLayoutManager(activity!!,3)
        return fragmentView

        fragmentView?.account_iv_profile?.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            activity?.startActivityForResult(photoPickerIntent,PICK_PROFILE_FROM_ALBUM)
        }
    }
    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener { querySnapshot, firebaseFirestore ->
                //Some times, This code return null of querySnapshot when it signout
                if(querySnapshot == null) return@addSnapshotListener

                //Get data
                for(snapshot in querySnapshot.documents){
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                fragmentView?.account_tv_post_count?.text = contentDTOs.size.toString()
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels / 3

            var imageview = ImageView(parent.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width,width)
            return CustomViewHolder(imageview)
        }

        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview) {

        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageview = (holder as CustomViewHolder).imageview
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl).apply(RequestOptions().centerCrop()).into(imageview)
        }

    }
}