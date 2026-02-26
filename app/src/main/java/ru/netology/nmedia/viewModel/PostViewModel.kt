package ru.netology.nmedia.viewModel

import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.util.SingleLiveEvent
import androidx.lifecycle.LiveData
import ru.netology.nmedia.model.FeedErrorMsg
import ru.netology.nmedia.model.ErrorMsg
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.model.FeedModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import android.net.Uri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapLatest
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.model.PhotoModel
import java.io.File
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

private val empty = Post(
    id = 0,
    author = "",
    authorId = 0,
    authorAvatar = "",
    content = "",
    published = "",
    likesCount = 0,
    sharesCount = 0,
    likedByMe = false,
    videoUrl = null
)

private val noPhoto = PhotoModel()

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    auth: AppAuth,
) : ViewModel() {
    val data: LiveData<FeedModel> = auth.authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.data
                .map { posts ->
                    FeedModel(
                        posts.map { it.copy(ownedByMe = it.authorId == myId) },
                        posts.isEmpty()
                    )
                }
        }.asLiveData(Dispatchers.Default)
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)
    }
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAllAsync()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun updateIsRead() = viewModelScope.launch {
        repository.updateIsRead()
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAllAsync()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    when(_photo.value) {
                        noPhoto -> repository.saveAsync(it)
                        else -> _photo.value?.file?.let { file ->
                            repository.saveWithAttachment(it, MediaUpload(file))
                        }
                    }
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
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
        viewModelScope.launch {
            try {
                if (isLiked) repository.unlikeByIdAsync(id) else repository.likeByIdAsync(id)
                _dataState.value = FeedModelState(errorMsg = null)
            } catch (_: RuntimeException) {
                if (!isLiked) {
                    _dataState.value = FeedModelState(
                        errorMsg = ErrorMsg(
                            id,
                            FeedErrorMsg.LIKE_ERROR
                        )
                    )
                } else {
                    _dataState.value = FeedModelState(
                        errorMsg = ErrorMsg(
                            id,
                            FeedErrorMsg.UNLIKE_ERROR
                        )
                    )
                }
            }
        }
    }

    fun shareById(id: Long) = repository.shareById(id)

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeByIdAsync(id)

        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }
}