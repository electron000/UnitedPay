package com.unitedpay.feature.home.services.promotions

import com.unitedpay.core.designsystem.components.UnitedToast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.model.OfferDealItem
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.designsystem.theme.*

typealias OfferDeal = OfferDealItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OffersScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedCategory by remember { mutableStateOf("All Deals") }
    var copiedCode by remember { mutableStateOf<String?>(null) }
    var categories by remember { mutableStateOf(com.unitedpay.core.model.mock.UnitedMockData.offerCategories) }
    var offers by remember { mutableStateOf(com.unitedpay.core.model.mock.UnitedMockData.offerDeals) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.promotions.getOfferCategories().collect { res ->
            if (res is Resource.Success) categories = res.data
        }
    }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.promotions.getOfferDeals().collect { res ->
            if (res is Resource.Success) offers = res.data
        }
    }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Offers & Cashback", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Category Filter Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) UnitedMoneyBlue else UnitedWhite)
                                .border(1.dp, if (isSelected) UnitedMoneyBlue else UnitedBorderLight, RoundedCornerShape(6.dp))
                                .clickable { selectedCategory = cat }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) UnitedWhite else UnitedTextPrimary
                            )
                        }
                    }
                }
            }

            // Banner Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D256C))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(UnitedLimeAccent)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("EXCLUSIVE DEAL", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Win ₹500 Cashback on 5th UPI payment", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = UnitedWhite)
                            Text("Scan any merchant QR to unlock reward", fontSize = 11.sp, color = UnitedWhite.copy(alpha = 0.8f))
                        }
                    }
                }
            }

            // Offers List
            items(offers) { offer ->
                val isCopied = copiedCode == offer.code
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
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
                            Text(offer.brand, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = UnitedTextPrimary)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(UnitedSuccessContainer)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(offer.discountPill, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedSuccess)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(offer.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = UnitedTextPrimary)
                        Text(offer.desc, fontSize = 11.5.sp, color = UnitedTextSecondary)

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Coupon Code Box
                            Box(
                                modifier = Modifier
                                    .border(1.dp, UnitedMoneyBlue.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF0F4FF))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = offer.code,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    letterSpacing = 1.sp,
                                    color = UnitedMoneyBlue
                                )
                            }

                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(offer.code))
                                    copiedCode = offer.code
                                    UnitedToast.success("Code ${offer.code} copied!")
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCopied) UnitedSuccess else UnitedMoneyBlue,
                                    contentColor = UnitedWhite
                                )
                            ) {
                                Icon(
                                    imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = UnitedWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isCopied) "Copied!" else "Copy Code", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = UnitedWhite)
                            }
                        }
                    }
                }
            }
        }
    }
}
