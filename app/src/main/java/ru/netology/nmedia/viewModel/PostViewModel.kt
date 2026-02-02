package ru.netology.nmedia.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import androidx.lifecycle.LiveData
import ru.netology.nmedia.model.FeedErrorMsg
import ru.netology.nmedia.model.ErrorMsg

private val empty = Post(
    id = 0,
    author = "",
    authorAvatar = "",
    content = "",
    published = "",
    likesCount = 0,
    sharesCount = 0,
    likedByMe = false,
    videoUrl = null
)
class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
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
        repository.getAllAsync(object : PostRepository.PostCallback<List<Post>> {
            override fun onSuccess(result: List<Post>) {
                _data.postValue(FeedModel(posts = result, empty = result.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let {
            repository.saveAsync(it, object : PostRepository.PostCallback<Post> {
                override fun onSuccess(result: Post) {

                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    _data.value = _data.value?.copy(
                        errorMsg = ErrorMsg(0, FeedErrorMsg.SAVE_ERROR))
                }
            })
        }
        edited.value = empty
    }


    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }
    fun edit(post: Post) {
        edited.value = post
    }

    fun likeById(id: Long) {
        val isLiked = data.value?.posts?.find { it.id == id }?.likedByMe ?: return
        if (!isLiked) {
            repository.likeByIdAsync(id, object : PostRepository.PostCallback<Post> {
                override fun onSuccess(result: Post) {
                    _data.postValue(
                        _data.value?.copy(
                            posts = _data.value?.posts.orEmpty().map {
                                if (it.id == id) {
                                    result
                                } else it
                            }
                        )
                    )
                }

                override fun onError(e: Exception) {
                    _data.value = _data.value?.copy(
                        errorMsg = ErrorMsg(id, FeedErrorMsg.LIKE_ERROR))
                }
            })
        } else {
            repository.unlikeByIdAsync(id, object : PostRepository.PostCallback<Post> {
                override fun onSuccess(result: Post) {
                    _data.postValue(
                        _data.value?.copy(
                            posts = _data.value?.posts.orEmpty().map {
                                if (it.id == id) {
                                    result
                                } else it
                            }
                        )
                    )
                }

                override fun onError(e: Exception) {
                    _data.value = _data.value?.copy(
                        errorMsg = ErrorMsg(id, FeedErrorMsg.UNLIKE_ERROR))
                }
            })
        }
    }


    fun shareById(id: Long) = repository.shareById(id)

    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        _data.postValue(
            _data.value?.copy(
                posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
            )
        )
        repository.removeByIdAsync(id, object : PostRepository.PostCallback<Unit> {
            override fun onSuccess(result: Unit) {
                _data.postValue(
                    _data.value?.copy(
                        posts = _data.value?.posts.orEmpty().filter { it.id != id })
                )
            }

            override fun onError(e: Exception) {
                _data.value = _data.value?.copy(posts = old,
                    errorMsg = ErrorMsg(id, FeedErrorMsg.REMOVE_ERROR)
                )
            }
        })
    }
}