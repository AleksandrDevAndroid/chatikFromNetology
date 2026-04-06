package ru.netology.nmedia.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = "",
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
                _data.value = (FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Throwable) {
                _data.value = (FeedModel(error = true))
            }
        })
    }


    fun likeById(id: Long) {
        val post = _data.value?.posts?.find { it.id == id } ?: return
        if (!post.likedByMe) {
            repository.likeById(id, object : PostRepository.GetPostCallback {
                override fun onSuccess(result: Post) {
                    val refreshState = _data.value ?: return
                    val updatedPosts = refreshState.posts.map {
                        if (it.id == result.id) result else it
                    }
                    _data.postValue(refreshState.copy(posts = updatedPosts))
                    loadPosts()
                }
                override fun onError(error: Throwable) {
                    _data.value
                }
            })
        } else {

            repository.disLikeById(id, object : PostRepository.GetPostCallback {
                override fun onSuccess(result: Post) {
                    val refreshState = _data.value ?: return
                    val updatedPosts = refreshState.posts.map {
                        if (it.id == result.id) result else it
                    }
                    _data.postValue(refreshState.copy(posts = updatedPosts))
                    loadPosts()
                }

                override fun onError(error: Throwable) {
                    _data.value
                }
            })
        }
    }

        fun save() {
            edited.value?.let {
                repository.savePost(it, object : PostRepository.GetPostCallback {
                    override fun onSuccess(post: Post) {

                        repository.getAllAsync(object : PostRepository.GetAllCallback {
                            override fun onSuccess(posts: List<Post>) {
                                try {

                                    _postCreated.postValue(Unit)
                                } catch (e: Exception) {
                                    onError(e)
                                    edited.value = empty
                                }
                            }

                            override fun onError(e: Throwable) {
                                Toast.makeText(
                                    getApplication(),
                                    "Произошла ошибка при сохранении поста",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                        edited.value = empty
                    }
                })
            }
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

                override fun onError(e: Throwable) {
                    _data.postValue(_data.value?.copy(posts = old))
                    Toast.makeText(
                        getApplication(),
                        "Произошла ошибка при удалении поста",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

