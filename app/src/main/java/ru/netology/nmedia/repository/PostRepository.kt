package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll() : List<Post>
    fun likeById(id: Long,callback: GetPostCallback)
    fun disLikeById(id: Long,callback: GetPostCallback)
    fun savePost(post: Post,callback: GetPostCallback)
    fun removeById(id: Long)
    fun getAllAsync(callback: GetAllCallback)


    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Throwable) {}
    }

    interface GetPostCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Throwable) {}
    }
}
