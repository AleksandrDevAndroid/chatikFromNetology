package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }


    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))
        repository.getAllAsync(object : PostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }


    fun likeById(id: Long) {
        repository.getAllAsync(object : PostRepository.GetAllCallback {
            val likedByMe = data.value?.posts?.find { it.id == id }?.likedByMe
            override fun onSuccess(posts: List<Post>) {
                if (likedByMe != true) repository.likeById(id)
                else repository.disLikeById(id)
            }
        })
    }


            fun save() {
                edited.value?.let {
                    repository.getAllAsync(object : PostRepository.GetAllCallback {
                        override fun onSuccess(posts: List<Post>) {
                            try {
                                repository.save(it)
                                _postCreated.postValue(Unit)
                            } catch (e: Exception) {
                                onError(e)
                                edited.value = empty
                            }
                        }
                    })
                }
                edited.value = empty
            }

            fun edit(post: Post) {
                edited.value = post
            }

            fun changeContent(content: String) {
                val text = content.trim()
                if (edited.value?.content == text) {
                    return
                }
                edited.value = edited.value?.copy(content = text)
            }


            fun removeById(id: Long) {
                val old = _data.value?.posts.orEmpty()
                _data.postValue(
                    _data.value?.copy(
                        posts = _data.value?.posts.orEmpty().filter { it.id != id })
                )
                repository.getAllAsync(object : PostRepository.GetAllCallback {
                    override fun onSuccess(posts: List<Post>) {
                        repository.removeById(id)
                    }

                    override fun onError(e: Exception) {
                        _data.postValue(_data.value?.copy(posts = old))
                    }
                })
            }
        }

