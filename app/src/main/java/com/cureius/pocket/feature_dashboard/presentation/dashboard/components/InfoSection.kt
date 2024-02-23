package com.cureius.pocket.feature_dashboard.presentation.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cureius.pocket.R
import com.cureius.pocket.feature_dashboard.presentation.dashboard.DashBoardViewModel
import com.cureius.pocket.feature_transaction.presentation.transactions.TransactionsViewModel


fun Modifier.vertical() = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(placeable.height, placeable.width) {
        placeable.place(
            x = -(placeable.width / 2 - placeable.height / 2),
            y = -(placeable.height / 2 - placeable.width / 2)
        )
    }
}

@Preview
@Composable
fun InfoSection(viewModel: DashBoardViewModel = hiltViewModel(), transactionsViewModel: TransactionsViewModel = hiltViewModel()) {
    val rupee = painterResource(id = R.drawable.rupee)
    val shape = RoundedCornerShape(
        topStart = 24.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 24.dp
    )
    val mtdShape = RoundedCornerShape(
        topStart = 0.dp, topEnd = 0.dp, bottomStart = 24.dp, bottomEnd = 0.dp
    )
    val transactions = transactionsViewModel.state.value.transactionsOnCurrentMonthForAccounts
    val totalIncomeAmount = transactions.filter { it.type == "credited" }.sumOf { it.amount!! }
    val totalSpentAmount = transactions.filter { it.type == "debited" }.sumOf { it.amount!! }
    val totalMTDBalance = totalIncomeAmount - totalSpentAmount
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp, 16.dp, 12.dp),
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.primary.copy(alpha = 0.1f), shape)
                .fillMaxWidth()
                .padding(8.dp, 8.dp, 8.dp, 0.dp)
                .height(108.dp)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxWidth(0.7f)) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp, 8.dp, 4.dp, 12.dp)
                            .fillMaxWidth(0.9f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "You Have",
                            color = MaterialTheme.colors.onBackground,
                            textAlign = TextAlign.Center,
                            style = TextStyle(fontWeight = FontWeight.Bold),
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(0.dp, 0.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rupee,
                                contentDescription = "rupee",
                                modifier = Modifier.size(16.dp),
                                colorFilter = ColorFilter.tint(MaterialTheme.colors.primaryVariant),
                                alignment = Alignment.Center
                            )
                            Text(
                                text = " $totalMTDBalance",
                                color = MaterialTheme.colors.primary,
                                textAlign = TextAlign.Center,
                                style = TextStyle(fontWeight = FontWeight.Bold),
                                fontSize = 20.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(0.dp, 0.dp)
                            )
                        }
                    }
                    Canvas(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        drawLine(
                            color = Color.DarkGray.copy(alpha = 0.5f),
                            start = Offset(x = 0f, y = size.height / 2),
                            end = Offset(x = size.width * 0.95f, y = size.height / 2),
                            strokeWidth = 6f,
                            pathEffect = PathEffect.dashPathEffect(
                                intervals = floatArrayOf(20f, 10f), phase = 0f
                            )
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box {
                            Box(
                                modifier = Modifier
                                    .offset(x = (-8).dp, y = (0).dp)
                                    .background(
                                        MaterialTheme.colors.primary.copy(alpha = 0.1f), mtdShape
                                    )
                                    .fillMaxHeight()
                                    .width(24.dp),
                            )
                            Text(
                                modifier = Modifier
                                    .vertical()
                                    .rotate(-90f)
                                    .offset(y = (-8).dp)
                                    .padding(2.dp, 0.dp, 8.dp, 2.dp),
                                text = "MTD",
                                style = TextStyle(fontWeight = FontWeight.Light),
                                color = MaterialTheme.colors.background,
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 0.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = rupee,
                                        contentDescription = "rupee",
                                        modifier = Modifier.size(12.dp),
                                        colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                                        alignment = Alignment.Center
                                    )
                                    Text(
                                        text = " $totalIncomeAmount",
                                        color = com.cureius.pocket.ui.theme.Green,
                                        textAlign = TextAlign.Center,
                                        style = TextStyle(fontWeight = FontWeight.Normal),
                                        fontSize = 16.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(0.dp, 0.dp)
                                    )
                                }

                                Text(
                                    text = "INCOME",
                                    color = MaterialTheme.colors.onSurface,
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(fontWeight = FontWeight.Normal),
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(0.dp, 0.dp)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = rupee,
                                        contentDescription = "rupee",
                                        modifier = Modifier.size(12.dp),
                                        colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                                        alignment = Alignment.Center
                                    )
                                    Text(
                                        text = " $totalSpentAmount",
                                        color = com.cureius.pocket.ui.theme.TomatoRed,
                                        textAlign = TextAlign.Center,
                                        style = TextStyle(fontWeight = FontWeight.Normal),
                                        fontSize = 16.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(0.dp, 0.dp)
                                    )
                                }

                                Text(
                                    text = "SPENT",
                                    color = MaterialTheme.colors.onSurface,
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(fontWeight = FontWeight.Normal),
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(0.dp, 0.dp)
                                )
                            }

                        }
                    }

                }
                Box(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val pngImage: Painter = painterResource(id = R.drawable.sc2)
                        Image(
                            painter = pngImage,
                            contentDescription = "Scan & Pay",
                            modifier = Modifier.size(90.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface.copy(alpha = 0.8f))
                        )
                        Text(
                            text = "Scan & Pay",
                            color = MaterialTheme.colors.onPrimary,
                            textAlign = TextAlign.Center,
                            style = TextStyle(fontWeight = FontWeight.Bold),
                            fontSize = 10.sp
                        )
                    }

                }
            }
        }
    }
}

@Composable
@Preview
fun InfoSectionPreview() {
    InfoSection()
}