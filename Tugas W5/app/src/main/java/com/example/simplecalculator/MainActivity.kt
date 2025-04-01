package com.example.simplecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.grid.items as gridExtensionItems
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.simplecalculator.ui.theme.SimpleCalculatorTheme
import java.util.Locale
import kotlin.math.abs

enum class ButtonType {
    NUMBER, OPERATOR, FUNCTION, EQUALS, DECIMAL, BACKSPACE
}

enum class CalculatorMode {
    BASIC, TEMP_CONVERSION
}

data class ButtonData(
    val label: String,
    val type: ButtonType
)

data class CalculatorState(
    val displayValue: String = "0",
    val operand1: String = "",
    val operator: String? = null,
    val operand2: String = ""
)

enum class FocusedTempField {
    CELSIUS, FAHRENHEIT, NONE
}

fun getCalculatorButtons(): List<ButtonData> {
    return listOf(
        ButtonData("AC", ButtonType.FUNCTION),
        ButtonData("%", ButtonType.FUNCTION),
        ButtonData("\u232B", ButtonType.BACKSPACE),
        ButtonData("\u00F7", ButtonType.OPERATOR),

        ButtonData("7", ButtonType.NUMBER),
        ButtonData("8", ButtonType.NUMBER),
        ButtonData("9", ButtonType.NUMBER),
        ButtonData("\u0078", ButtonType.OPERATOR),

        ButtonData("4", ButtonType.NUMBER),
        ButtonData("5", ButtonType.NUMBER),
        ButtonData("6", ButtonType.NUMBER),
        ButtonData("-", ButtonType.OPERATOR),

        ButtonData("1", ButtonType.NUMBER),
        ButtonData("2", ButtonType.NUMBER),
        ButtonData("3", ButtonType.NUMBER),
        ButtonData("+", ButtonType.OPERATOR),

        ButtonData("00", ButtonType.NUMBER),
        ButtonData("0", ButtonType.NUMBER),
        ButtonData(".", ButtonType.DECIMAL),
        ButtonData("=", ButtonType.EQUALS)
    )
}

@Composable
fun CalcButton(buttonData: ButtonData, onClick: () -> Unit, modifier: Modifier) {
    val containerColour = when (buttonData.type) {
        ButtonType.FUNCTION, ButtonType.BACKSPACE -> Color(red = 107, green = 154, blue = 196)
        ButtonType.OPERATOR -> Color(red = 92, green = 158, blue = 173)
        ButtonType.EQUALS -> Color(red = 227, green = 151, blue = 116)
        else -> Color(red = 50, green = 98, blue = 115)
    }
    val contentColour = when (buttonData.type) {
        ButtonType.FUNCTION, ButtonType.BACKSPACE, ButtonType.OPERATOR, ButtonType.EQUALS -> Color.Black
        else -> Color.White
    }

    Button(
        onClick = onClick,
        modifier = modifier.height(115.dp).fillMaxWidth().border(0.4.dp, color = Color.Black),
        shape = RectangleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColour,
            contentColor = contentColour
        )
    ) {
        Text(
            text = buttonData.label,
            fontSize = 30.sp,
            color = contentColour,
        )
    }
}

@Composable
fun ModeSwitcher(text: String, onClick: () -> Unit, modifier: Modifier, currentMode: CalculatorMode) {
    val isActive = when (text) {
        "Basic Mode" -> currentMode == CalculatorMode.BASIC
        "Temperature Conv" -> currentMode == CalculatorMode.TEMP_CONVERSION
        else -> false
    }
    val backgroundColour = if (isActive) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    } else {
        Color.Transparent
    }

    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColour,
            contentColor = Color.Black
        )
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
        )
    }
}

private fun performCalculation(operand1Str: String, operator: String?, operand2Str: String): String {
    if (operator == null) return operand2Str

    return try {
        val operand1 = operand1Str.toDouble()
        val operand2 = operand2Str.toDouble()
        val resultDouble = when (operator) {
            "+" -> operand1 + operand2
            "-" -> operand1 - operand2
            "\u00F7" -> {
                if (operand2 == 0.0) {
                    return "Error: Div by 0"
                } else {
                    operand1 / operand2
                }
            }
            "\u0078" -> operand1 * operand2
            else -> return "Error: Invalid Operator"
        }

        val formattedResult = formatResult(resultDouble)
        return formattedResult
    } catch (e: NumberFormatException) {
        "Error: $e"
    }
}

private fun formatResult(result: Double): String {
    if (result.isNaN() || result.isInfinite()) {
        return "Error"
    }

    return if (abs(result) >= 1e10 || (abs(result) < 1e-3 && result != 0.0)) {
        String.format(Locale.US, "%.8E", result)
    } else {
        String.format(Locale.US, "%.10f", result).trimEnd('0').let {
            if (it.endsWith(".")) it.dropLast(1) else it
        }
    }
}

fun updateCalculatorState(currentState: CalculatorState, buttonData: ButtonData): CalculatorState {
    if (buttonData.type == ButtonType.NUMBER) {
        val newDisplayValue = if (currentState.displayValue == "0" || currentState.displayValue == "00" || currentState.displayValue.contains("Error")) {
            buttonData.label
        } else {
            currentState.displayValue + buttonData.label
        }
        return currentState.copy(displayValue = newDisplayValue)
    }
    else if (buttonData.type == ButtonType.OPERATOR) {
        if (currentState.displayValue.contains("Error")){
            return currentState
        }
        if (currentState.operand1.isNotEmpty() && currentState.operator != null && currentState.operand2.isEmpty()) {
            if(currentState.displayValue === "0"){
                return currentState.copy(operator = buttonData.label)
            } else {
                val result = performCalculation( // 5 + 5 + 5 -> (10) + 5 -> (15)
                    currentState.operand1,
                    currentState.operator,
                    currentState.displayValue
                )
                return currentState.copy(
                    operand1 = result,
                    operator = buttonData.label,
                    displayValue = "0",
                    operand2 = ""
                )
            }
        } else {
            return currentState.copy(
                operand1 = currentState.displayValue,
                operator = buttonData.label,
                displayValue = "0",
                operand2 = ""
            )
        }
    }
    else if (buttonData.type == ButtonType.EQUALS) {
        if (currentState.displayValue.contains("Error")){
            return currentState
        }
        if (currentState.operand2.isNotEmpty()){ // 5 + 5 = (10) = (15) = ...
            val result = performCalculation(
                currentState.operand1,
                currentState.operator,
                currentState.operand2
            )
            return currentState.copy(
                operand1 = result,
                displayValue = result
            )
        }
        else if (currentState.operand1.isNotEmpty() && currentState.operator != null && currentState.displayValue.isNotEmpty()) {
            var tempState = currentState.copy(operand2 = currentState.displayValue)
            val result = performCalculation(
                tempState.operand1,
                tempState.operator,
                tempState.operand2
            )
            return tempState.copy(
                operand1 = result,
                displayValue = result
            )
        } else {
            return currentState
        }
    } else if (buttonData.type == ButtonType.FUNCTION && buttonData.label == "AC") {
        return CalculatorState()
    } else if (buttonData.type == ButtonType.FUNCTION && buttonData.label == "%") {
        if (currentState.operand1.isNotEmpty() && (currentState.operator == "+" || currentState.operator == "-")) {
            val percentageValue = currentState.displayValue.toDoubleOrNull() ?: 0.0
            val operand1Value = currentState.operand1.toDoubleOrNull() ?: 0.0
            val percentageOfOperand1 = operand1Value * (percentageValue / 100.0)

            val result = when (currentState.operator) {
                "+" -> operand1Value + percentageOfOperand1
                "-" -> operand1Value - percentageOfOperand1
                else -> operand1Value
            }

            return currentState.copy(
                displayValue = formatResult(result).toString(),
                operand1 = formatResult(result).toString(),
                operator = null,
                operand2 = ""
            )
        } else {
            return currentState
        }
    } else if (buttonData.type == ButtonType.BACKSPACE) {
        if (currentState.displayValue == "0" || currentState.displayValue.contains("Error")) {
            return currentState
        } else {
            val newDisplayValue = currentState.displayValue.dropLast(1)
            val finalDisplayValue = if (newDisplayValue.isEmpty()) "0" else newDisplayValue
            return if(currentState.operand2.isNotEmpty()) {
                currentState.copy(
                    operand1 = "0",
                    operator = null,
                    displayValue = finalDisplayValue,
                    operand2 = ""
                )
            } else {
                currentState.copy(
                    displayValue = finalDisplayValue
                )
            }
        }
    } else if (buttonData.type == ButtonType.DECIMAL) {
        if (!currentState.displayValue.contains(".")) {
            val newDisplayValue = if (currentState.displayValue == "0" || currentState.displayValue.contains("Error")) {
                "0."
            } else {
                currentState.displayValue + "."
            }
            return currentState.copy(displayValue = newDisplayValue)
        }
    }
    return currentState
}

fun updateTempState(currentValue: String, buttonData: ButtonData): String {
    return when (buttonData.type) {
        ButtonType.NUMBER -> {
            val newValue = currentValue + buttonData.label
            if (newValue.length <= 12) newValue else currentValue
            }
        ButtonType.DECIMAL -> {
            if (!currentValue.contains(".")) {
                val newValue = if (currentValue.isEmpty() || currentValue == "-") currentValue + "0." else "$currentValue."

                if (newValue.length <= 12) newValue else currentValue
            } else {
                currentValue
            }
        }
        ButtonType.BACKSPACE -> {
            if (currentValue.isNotEmpty()) {
                currentValue.dropLast(1)
            } else {
                ""
            }
        }
        ButtonType.FUNCTION -> {
            if (buttonData.label == "AC") {
                ""
            } else {
                currentValue
            }
        }
        ButtonType.OPERATOR -> {
            if (buttonData.label == "-" && currentValue.isEmpty()) {
                "-"
            } else
                currentValue
        }
        else -> currentValue
    }
}

fun convertTemperature(value: Double?, mode: String): String {
    if (mode == "Celcius") {
        if (value == null) return ""
        val fahrenheit = (value * 9 / 5) + 32
        return String.format(Locale.US, "%.3f", fahrenheit)
            .trimEnd('0')
            .removeSuffix(".")
    }
    else if (mode == "Fahrenheit"){
        if (value == null) return ""
        val celsius = (value - 32) * 5 / 9
        return String.format(Locale.US, "%.3f", celsius)
            .trimEnd('0')
            .removeSuffix(".")
    }
    return "Error: Invalid Mode"
}

@Composable
fun Calculator(modifier: Modifier) {
    var calculatorState by remember { mutableStateOf(CalculatorState()) }
    var displayValue by remember { mutableStateOf("0") }
    var calculatorMode by remember { mutableStateOf(CalculatorMode.BASIC) }
    val buttonDatas = getCalculatorButtons()
    displayValue = calculatorState.displayValue

    var tempInput1 by remember { mutableStateOf("") }
    var tempInput2 by remember { mutableStateOf("") }
    val celsiusFocusRequester = remember { FocusRequester() }
    val fahrenheitFocusRequester = remember { FocusRequester() }
    val celsiusInteractionSource = remember { MutableInteractionSource() }
    val fahrenheitInteractionSource = remember { MutableInteractionSource() }
    var focusedField by remember { mutableStateOf(FocusedTempField.NONE) }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ModeSwitcher(
                text = "Basic Mode",
                onClick = { calculatorMode = CalculatorMode.BASIC },
                modifier = Modifier,
                currentMode = calculatorMode
            )
            ModeSwitcher(
                text = "Temperature Conv",
                onClick = { calculatorMode = CalculatorMode.TEMP_CONVERSION },
                modifier = Modifier,
                currentMode = calculatorMode
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(bottom = 16.dp, start = 24.dp, end = 24.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            when (calculatorMode) {
                CalculatorMode.BASIC -> {
                    val scrollState = rememberScrollState()
                    Text(
                        text = displayValue,
                        style = MaterialTheme.typography.displayMedium,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .horizontalScroll(scrollState, enabled = true),
                    )
                }
                CalculatorMode.TEMP_CONVERSION -> {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        OutlinedTextField(
                            value = tempInput1,
                            onValueChange = { },
                            label = { Text("Celcius") },
                            interactionSource = celsiusInteractionSource,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .focusRequester(celsiusFocusRequester)
                                .onFocusChanged { focusState ->
                                    if (focusState.toString() == "ActiveParent") {
                                        focusedField = FocusedTempField.CELSIUS
                                    } else if (!focusState.isFocused && focusedField == FocusedTempField.CELSIUS) {
                                        focusedField = FocusedTempField.NONE
                                    }
                                }
                                .clickable(
                                    interactionSource = celsiusInteractionSource,
                                    indication = null
                                ) {
                                    celsiusFocusRequester.requestFocus()
                                },
                            readOnly = true,
                        )
                        OutlinedTextField(
                            value = tempInput2,
                            onValueChange = { },
                            label = { Text("Fahrenheit") },
                            interactionSource = fahrenheitInteractionSource,
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(fahrenheitFocusRequester)
                                .onFocusChanged { focusState ->
                                    if (focusState.toString() == "ActiveParent") {
                                        focusedField = FocusedTempField.FAHRENHEIT
                                    } else if (!focusState.isFocused && focusedField == FocusedTempField.FAHRENHEIT) {
                                        focusedField = FocusedTempField.NONE
                                    }
                                }
                                .clickable(
                                    interactionSource = fahrenheitInteractionSource,
                                    indication = null
                                ) {
                                    fahrenheitFocusRequester.requestFocus()
                                },
                            readOnly = true,
                        )
                    }
                }
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = modifier.fillMaxWidth(),
            userScrollEnabled = false,
        ) {
            if (calculatorMode == CalculatorMode.BASIC) {
                gridExtensionItems(items = buttonDatas) { buttonData ->
                    CalcButton(
                        buttonData = buttonData,
                        onClick = {
                            calculatorState = updateCalculatorState(calculatorState, buttonData)
                          },
                        modifier = Modifier,
                    )
                }
            } else {
                gridExtensionItems(items = buttonDatas) { buttonData ->
                    CalcButton(
                        buttonData = buttonData,
                        onClick = {
                            if(focusedField == FocusedTempField.CELSIUS) {
                                tempInput1 = updateTempState(tempInput1, buttonData)
                                val celsiusValue = tempInput1.toDoubleOrNull()
                                tempInput2 = convertTemperature(celsiusValue, mode = "Celcius")
                            } else if(focusedField == FocusedTempField.FAHRENHEIT) {
                                tempInput2 = updateTempState(tempInput2, buttonData)
                                val fahreinheitValue = tempInput2.toDoubleOrNull()
                                tempInput1 = convertTemperature(fahreinheitValue, mode = "Fahrenheit")
                            }
                        },
                        modifier = Modifier,
                    )
                }
            }
        }
    }
}



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleCalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Calculator(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    SimpleCalculatorTheme {
        Calculator(modifier = Modifier)
    }
}