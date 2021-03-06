package otus.homework.coroutines

import kotlinx.coroutines.*
import java.net.SocketTimeoutException

class CatsPresenter(
    private val catsService: CatsService,
    private val catsImageService: CatsService
) {

    private var _catsView: ICatsView? = null
    private var presenterScope: CoroutineScope = CatsCoroutineScope

    fun onInitComplete() {
        presenterScope.launch {
            try {
                val factResponse =
                    withContext(Dispatchers.Default) { catsService.getCatFact() }
                val imageResponse =
                    withContext(Dispatchers.Default) { catsImageService.getCatImage() }

                _catsView?.populate(FactAndImage(factResponse.text, imageResponse.file))

            } catch (ex: Exception) {
                when (ex) {
                    is SocketTimeoutException -> {
                        _catsView?.showMessageResource(R.string.socket_timeout_ex_message)
                    }
                    else -> {
                        CrashMonitor.trackWarning()
                        _catsView?.showMessage(ex.message ?: "")
                    }
                }
            }
        }
    }

    fun attachView(catsView: ICatsView) {
        _catsView = catsView
    }

    fun detachView() {
        _catsView = null
        presenterScope.cancel()
    }
}