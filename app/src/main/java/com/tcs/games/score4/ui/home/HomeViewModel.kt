package com.tcs.games.score4.ui.home

import androidx.lifecycle.ViewModel
import com.tcs.games.score4.data.repository.UserRepository
import com.tcs.games.score4.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    val userFlow: StateFlow<UserData?> = userRepository.userFlow
}