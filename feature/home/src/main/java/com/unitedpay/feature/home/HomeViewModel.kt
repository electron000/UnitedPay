package com.unitedpay.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.database.dao.TransactionDao
import com.unitedpay.core.model.BankAccount
import com.unitedpay.core.model.UpiTransaction
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.model.session.UserSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val userVpa: String = "",
    val primaryAccount: BankAccount? = null,
    val recentTransactions: List<UpiTransaction> = emptyList(),
    val errorMessage: String? = null
)

class HomeViewModel(
    private val transactionDao: TransactionDao? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            UserSessionManager.currentSession.collect {
                loadDashboard()
            }
        }
    }

    private fun loadDashboard() {
        val currentProfile = UserSessionManager.getCurrentProfile()
        val currentAccounts = UserSessionManager.getCurrentBankAccounts()
        val currentTxns = UserSessionManager.getCurrentTransactions()

        _uiState.value = _uiState.value.copy(
            userName = currentProfile.userName,
            userVpa = currentProfile.vpa,
            primaryAccount = currentAccounts.firstOrNull { it.isPrimary } ?: currentAccounts.firstOrNull(),
            recentTransactions = currentTxns,
            isLoading = false
        )

        viewModelScope.launch {
            UnitedPayApi.client.banking.getUserProfile().collect { res ->
                if (res is Resource.Success) {
                    _uiState.value = _uiState.value.copy(
                        userName = res.data.userName,
                        userVpa = res.data.vpa
                    )
                }
            }
        }
        viewModelScope.launch {
            UnitedPayApi.client.banking.getLinkedBankAccounts().collect { res ->
                if (res is Resource.Success) {
                    _uiState.value = _uiState.value.copy(
                        primaryAccount = res.data.firstOrNull { it.isPrimary } ?: res.data.firstOrNull()
                    )
                }
            }
        }
        viewModelScope.launch {
            if (transactionDao != null) {
                transactionDao.getAllTransactions()
                    .map { list -> list.map { it.toDomain() } }
                    .collect { transactions ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            recentTransactions = transactions.ifEmpty { UserSessionManager.getCurrentTransactions() }
                        )
                    }
            } else {
                UnitedPayApi.client.banking.getRecentTransactions().collect { res ->
                    if (res is Resource.Success) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            recentTransactions = res.data
                        )
                    }
                }
            }
        }
    }
}
