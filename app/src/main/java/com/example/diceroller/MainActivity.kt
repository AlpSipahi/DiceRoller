package com.example.diceroller

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diceroller.ui.theme.DiceRollerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceRollerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DiceRollerApp(this)
                }
            }
        }
    }
}

@Composable
fun DiceRollerApp(context: Context) {
    var selectedDice by remember { mutableStateOf(listOf(6)) }
    var diceResults by remember { mutableStateOf(List(selectedDice.size) { 1 }) }
    val diceOptions = listOf(4, 6, 8, 10, 12, 20)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            items(diceOptions) { dice ->
                DiceSelector(
                    dice = dice,
                    selectedDice = selectedDice,
                    onDiceSelected = {
                        selectedDice = if (it in selectedDice) {
                            selectedDice - it
                        } else {
                            selectedDice + it
                        }
                    }
                )
            }
        }

        DiceWithButtonAndImage(
            selectedDice = selectedDice,
            onRoll = { diceResults = List(selectedDice.size) { (1 until selectedDice[it] + 1).random() } },
            context = context,
            results = diceResults
        )

        Results(results = diceResults)
    }
}

@Composable
fun Results(results: List<Int>) {
    if (results.isNotEmpty()) {
        Text(
            text = results.joinToString(", "),
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Composable
fun DiceWithButtonAndImage(
    selectedDice: List<Int>,
    onRoll: () -> Unit,
    context: Context,
    results: List<Int>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        for ((rowIndex, diceRow) in selectedDice.chunked(2).withIndex()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                for ((index, dice) in diceRow.withIndex()) {
                    val resultIndex = rowIndex * 2 + index
                    DrawDiceWithNumber(dice, results.getOrElse(resultIndex) { 1 }, context, Modifier.size(140.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Dynamically generate the range based on the selected dice
            onRoll()
        }) {
            Text(stringResource(id = R.string.roll))
        }
    }
}

@Composable
fun DrawDiceWithNumber(dice: Int, result: Int, context: Context, modifier: Modifier = Modifier) {
    val imageModifier = modifier
        .clip(MaterialTheme.shapes.medium)
        .background(MaterialTheme.colorScheme.background)

    val painter = painterResource(id = getDrawableResourceForDice(dice, result, context))

    Box(
        modifier = imageModifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = result.toString(),
            fontSize = 24.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DiceSelector(
    dice: Int,
    selectedDice: List<Int>,
    onDiceSelected: (Int) -> Unit
) {
    val isSelected = dice in selectedDice

    Box(
        modifier = Modifier
            .height(60.dp)
            .width(60.dp)
            .padding(4.dp)
            .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f)
            )
            .toggleable(
                value = isSelected,
                onValueChange = { onDiceSelected(dice) }
            )
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "D$dice", fontSize = 16.sp)
    }
}



@Composable
fun getDrawableResourceForDice(dice: Int, result: Int, context: Context): Int {
    return when (dice) {
        4 -> R.drawable.d4_blank
        6 -> R.drawable.d6_blank
        8 -> R.drawable.d8_blank
        10 -> R.drawable.d10_blank
        12 -> R.drawable.d12_blank
        20 -> R.drawable.d20_blank
        else -> throw IllegalArgumentException("Unsupported dice: D$dice")
    }
}