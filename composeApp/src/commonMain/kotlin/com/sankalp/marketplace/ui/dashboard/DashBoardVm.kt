package com.sankalp.marketplace.ui.dashboard

import androidx.lifecycle.ViewModel
import com.sankalp.marketplace.data.repository.ProductRepo
import com.sankalp.marketplace.data.repository.UserRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class DashBoardVm(
    private val productRepo : ProductRepo,
    private val userRepo : UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DashBoardState())
    val state = _state.asStateFlow()
    private val _effect = Channel<DashBoardEffect?>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onEvent(event : DashBoardEvent){
        when(event){
            is DashBoardEvent.OnNavItemClick -> {
                _state.update {
                    it.copy(
                        selectedNavItem = event.navItem
                    )
                }
            }
        }
    }
}