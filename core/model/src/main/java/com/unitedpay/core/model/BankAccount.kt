package com.unitedpay.core.model

data class BankAccount(
    val id: String,
    val bankName: String,
    val ifscCode: String,
    val accountNumberMasked: String,
    val accountType: String = "SAVINGS",
    val isPrimary: Boolean = false,
    val balance: Double? = null,
    val logoUrl: String? = null
)

data class UserProfile(
    val userId: String,
    val fullName: String,
    val phoneNumber: String,
    val primaryVpa: String,
    val qrCodePayload: String,
    val isKycVerified: Boolean = true,
    val avatarUrl: String? = null
) {
    val userName: String get() = fullName
    val vpa: String get() = primaryVpa
}
