package ru.netology.nmedia.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.application

private val empty = Post(
    id = 0,
    author = "",
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

//    fun loadPosts() {
//        thread {
//            // Начинаем загрузку
//            _data.postValue(FeedModel(loading = true))
//            try {
//                // Данные успешно получены
//                val posts = repository.getAll()
//                FeedModel(posts = posts, empty = posts.isEmpty())
//            } catch (e: IOException) {
//                // Получена ошибка
//                FeedModel(error = true)
//            }.also(_data::postValue)
//        }
//    }

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

//    fun save() {
//        edited.value?.let {
//            thread {
//                repository.save(it)
//                _postCreated.postValue(Unit)
//            }
//        }
//        edited.value = empty
//    }

//    fun save(content: Int) {
//        edited.value?.let { editPost ->
//            val text = content.trim()
//
//            if (text != editPost.content) {
//                repository.saveAsync(
//                    editPost.copy(content = text),
//                    object : PostRepository.PostCallback<Post> {
//                        override fun onSuccess(result: Post) {
//                            if (editPost.id == 0L) {
//                                val newListPosts = listOf(result) + _data.value?.posts.orEmpty()
//                                _data.postValue(
//                                    _data.value?.copy(
//                                        posts = newListPosts
//                                    )
//                                )
//                            } else {
//                                val newListPosts = _data.value?.posts.orEmpty().map {
//                                    if (it.id == result.id) {
//                                        result
//                                    } else it
//                                }
//                                _data.postValue(
//                                    _data.value?.copy(
//                                        posts = newListPosts
//                                    )
//                                )
//                            }
//
//                            _postCreated.postValue(Unit)
//                        }
//
//                        override fun onError(e: Exception) {
//                            showErrorToast("Ошибка добавления поста")
//                            _postCreated.postValue(Unit)
//                        }
//
//                    })
//            }
//        }
//    }

    fun save() {
        edited.value?.let {
            repository.saveAsync(it, object : PostRepository.PostCallback<Post> {
                override fun onSuccess(result: Post) {

                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    _data.value

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

//    fun likeById(id: Long) {
//        val isLiked = data.value?.posts?.find { it.id == id }?.likedByMe ?: return
//        thread {
//            if (!isLiked) {
//                repository.likeById(id)
//            } else {
//                repository.unlikeById(id)
//            }
//            loadPosts()
//        }
//    }

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
                    showErrorToast("Ошибка добавления лайка")
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
                    showErrorToast("Ощибка удаления лайка")
                }
            })
        }
        loadPosts()
    }


    fun shareById(id: Long) = repository.shareById(id)

//    fun removeById(id: Long) {
//        thread {
//            // Оптимистичная модель
//            val old = _data.value?.posts.orEmpty()
//            _data.postValue(
//                _data.value?.copy(posts = _data.value?.posts.orEmpty()
//                    .filter { it.id != id }
//                )
//            )
//            try {
//                repository.removeById(id)
//            } catch (e: IOException) {
//                _data.postValue(_data.value?.copy(posts = old))
//            }
//        }
//    }

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
                _data.postValue(_data.value?.copy(posts = old))
                showErrorToast("Ошибка удаления поста")
            }
        })
    }

    private fun showErrorToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(application, message, Toast.LENGTH_LONG).show()
        }
    }
}