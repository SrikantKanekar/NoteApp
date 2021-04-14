package com.example.note.framework.presentation.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.note.framework.presentation.components.CircularButton

@Composable
fun MySuccessDialog(
    modifier: Modifier = Modifier,
    message: String,
    removeStateMessage: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        title = { Text(text = "Success") },
        text = { Text(text = message) },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalArrangement = Arrangement.Center,
                ) {
                CircularButton(
                    text = "Ok",
                    onClick = { removeStateMessage() },
                )
            }
        },
        onDismissRequest = {
            removeStateMessage()
        }
    )
}