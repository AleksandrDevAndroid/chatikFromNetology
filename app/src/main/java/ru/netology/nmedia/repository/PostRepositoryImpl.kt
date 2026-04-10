package ru.netology.nmedia.repository


import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.API.PostAPI
import ru.netology.nmedia.dto.Post


class PostRepositoryImpl : PostRepository {

    override fun getAll(): List<Post> = PostAPI.service.getAll().execute().body().orEmpty()


    override fun likeById(id: Long, callback: PostRepository.GetPostCallback) {
        PostAPI.service.likePost(id).enqueue(object : Callback<Post> {
            override fun onResponse(
                call: Call<Post?>, response: Response<Post?>
            ) {
                if (!response.isSuccessful) {
                    when (response.code()) {
                        in 400..499 -> callback.onError(RuntimeException("Post not found"))
                        in 500..599 -> callback.onError(RuntimeException("Server error"))
                        else -> callback.onError(RuntimeException("Error: ${response.code()}"))
                    }
                    return

                } else {
                    val post = response.body() ?: return
                    callback.onSuccess(post)
                }
            }

            override fun onFailure(
                call: Call<Post?>, t: Throwable
            ) {
                callback.onError(t)
            }

        })
    }


    override fun disLikeById(id: Long, callback: PostRepository.GetPostCallback) {
        PostAPI.service.dislikePost(id).enqueue(object : Callback<Post> {
            override fun onResponse(
                call: Call<Post?>, response: Response<Post?>
            ) {
                if (!response.isSuccessful) {
                    when (response.code()) {
                        in 400..499 -> callback.onError(RuntimeException("Post not found"))
                        in 500..599 -> callback.onError(RuntimeException("Server error"))
                        else -> callback.onError(RuntimeException("Error: ${response.code()}"))
                    }
                    return

                } else {
                    val post = response.body() ?: return
                    callback.onSuccess(post)

                }
            }

            override fun onFailure(
                call: Call<Post?>, t: Throwable
            ) {
                callback.onError(t)
            }
        })
    }

    override fun savePost(post: Post, callback: PostRepository.GetPostCallback) {
        PostAPI.service.savePost(post).enqueue(object : Callback<Post> {
            override fun onResponse(
                call: Call<Post?>, response: Response<Post?>
            ) {
                if (!response.isSuccessful) {
                    when (response.code()) {
                        in 400..499 -> callback.onError(RuntimeException("Post not found"))
                        in 500..599 -> callback.onError(RuntimeException("Server error"))
                        else -> callback.onError(RuntimeException("Error: ${response.code()}"))
                    }
                    return

                } else {
                    val post = response.body() ?: return
                    callback.onSuccess(post)
                }
            }
            override fun onFailure(
                call: Call<Post?>, t: Throwable
            ) {
                callback.onError(t)
            }
        })
    }

    override fun removeById(id: Long) {
        PostAPI.service.deletePost(id).enqueue(object : Callback<Unit> {
            override fun onResponse(
                call: Call<Unit?>, response: Response<Unit?>
            ) {
            }

            override fun onFailure(
                call: Call<Unit?>, t: Throwable
            ) {
            }
        })
    }

    override fun getAllAsync(callback: PostRepository.GetAllCallback) {
        PostAPI.service.getAll().enqueue(object : Callback<List<Post>> {
            override fun onResponse(
                call: Call<List<Post>>, response: Response<List<Post>>
            ) {
                if (!response.isSuccessful) {
                    when (response.code()) {
                        in 400..499 -> callback.onError(RuntimeException("Post not found"))
                        in 500..599 -> callback.onError(RuntimeException("Server error"))
                        else -> callback.onError(RuntimeException("Error: ${response.code()}"))
                    }
                    return

                } else {
                    callback.onSuccess(response.body().orEmpty())
                }

            }

            override fun onFailure(
                call: Call<List<Post>?>, t: Throwable
            ) {
                callback.onError(t)
            }
        })
    }
}
