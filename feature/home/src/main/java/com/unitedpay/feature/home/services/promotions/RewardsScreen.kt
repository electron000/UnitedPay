package com.unitedpay.feature.home.services.promotions

import com.unitedpay.core.designsystem.components.UnitedToast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.designsystem.components.UnitedEmptyState
import com.unitedpay.core.model.ScratchCardReward
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.model.session.UserSessionManager
import com.unitedpay.core.designsystem.theme.*

typealias ScratchCardItem = ScratchCardReward

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var scratchCards by remember {
        mutableStateOf<List<ScratchCardReward>>(UserSessionManager.getCurrentScratchCards())
    }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.promotions.getScratchCards().collect { res ->
            if (res is Resource.Success) scratchCards = res.data
        }
    }

    val hasRewards = scratchCards.isNotEmpty()
    val totalCashback = if (hasRewards) "₹1,450" else "₹0"
    val coins = if (hasRewards) "2,850 United Coins" else "0 United Coins"
    val milestoneText = if (hasRewards) "3 / 5 Payments" else "0 / 5 Payments"
    val milestoneProgress = if (hasRewards) 0.6f else 0.0f
    val milestoneDesc = if (hasRewards) "Complete 2 more payments to unlock Diamond Scratch Card up to ₹500" else "Complete 5 payments to unlock Diamond Scratch Card up to ₹500"

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Rewards & Scratch Cards", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = UnitedTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = UnitedWhite,
                    titleContentColor = UnitedTextPrimary,
                    navigationIconContentColor = UnitedTextPrimary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Total Rewards Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = UnitedHeaderBlueDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("TOTAL REWARDS EARNED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedWhite.copy(alpha = 0.75f), letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(totalCashback, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = UnitedWhite)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(UnitedLimeAccent)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(coins, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            }
                        }
                    }
                }
            }

            // Milestone Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = UnitedWhite)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Monthly Milestone", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = UnitedTextPrimary)
                            Text(milestoneText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = UnitedMoneyBlue)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFE2E8F0))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(milestoneProgress)
                                    .fillMaxSize()
                                    .background(UnitedMoneyBlue)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(milestoneDesc, fontSize = 11.5.sp, color = UnitedTextSecondary)
                    }
                }
            }

            if (scratchCards.isEmpty()) {
                item {
                    UnitedEmptyState(
                        icon = Icons.Default.Star,
                        title = "No Scratch Cards Yet",
                        subtitle = "Make your first UPI payment, recharge, or bill payment to earn cashback and scratch cards!"
                    )
                }
            } else {
                // Scratch Cards Grid Title
                item {
                    Text("Your Scratch Cards (Tap to reveal)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = UnitedTextPrimary)
                }

                // 2x2 Scratch Cards Grid
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        scratchCards.take(2).forEach { card ->
                            Box(modifier = Modifier.weight(1f)) {
                                ScratchCardTile(card = card, onScratch = {
                                    scratchCards = scratchCards.map { if (it.id == card.id) it.copy(isScratched = true) else it }
                                    UnitedToast.success("Won ${card.rewardText}!")
                                })
                            }
                        }
                    }
                }

                if (scratchCards.size > 2) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            scratchCards.drop(2).take(2).forEach { card ->
                                Box(modifier = Modifier.weight(1f)) {
                                    ScratchCardTile(card = card, onScratch = {
                                        scratchCards = scratchCards.map { if (it.id == card.id) it.copy(isScratched = true) else it }
                                        UnitedToast.success("Won ${card.rewardText}!")
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScratchCardTile(card: ScratchCardItem, onScratch: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable(enabled = !card.isScratched, onClick = onScratch),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (card.isScratched) Color(0xFFF0FDF4) else Color(0xFF1E3A8A)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, if (card.isScratched) UnitedSuccess.copy(alpha = 0.4f) else Color.Transparent, RoundedCornerShape(12.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            if (card.isScratched) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = UnitedSuccess, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(card.rewardText, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = UnitedSuccess)
                    Text("Credited to Bank", fontSize = 10.sp, color = UnitedTextSecondary)
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = UnitedLimeAccent, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(card.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedWhite, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("TAP TO SCRATCH", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = UnitedLimeAccent)
                }
            }
        }
    }
}
