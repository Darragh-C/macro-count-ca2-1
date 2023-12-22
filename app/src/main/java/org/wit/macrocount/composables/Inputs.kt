package org.wit.macrocount.composables


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicTextInput(text: String, onDescChanged: (String) -> Unit, label: String, placeholder: String) {
    OutlinedTextField(
        value = text,
        onValueChange = { onDescChanged(it) },
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        trailingIcon = {
            Icon(
                Icons.Default.Edit, contentDescription = "add/edit",
                tint = Color.Black
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleInput(title: String, onTitleChanged: (String) -> Unit, showError: Boolean) {
    OutlinedTextField(
        value = title,
        onValueChange = { onTitleChanged(it) },
        label = { Text("Title") },
        placeholder = { Text("Enter a title") },
        trailingIcon = {
            if (showError)
                Icon(
                    Icons.Filled.Warning, "error",
                    tint = MaterialTheme.colorScheme.error
                )
            else
                Icon(
                    Icons.Default.Edit, contentDescription = "add/edit",
                    tint = Color.Black
                )
        },
        isError = showError,
        supportingText = { ShowSupportText(showError) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
    )
}

@Composable
fun ShowSupportText(isError: Boolean) {
    if (isError)
        Text(
            text = "This field is required",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.error,
        )
    else Text(text = "")
}