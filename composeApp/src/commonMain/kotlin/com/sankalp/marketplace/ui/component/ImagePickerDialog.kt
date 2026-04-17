package com.sankalp.marketplace.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sankalp.marketplace.utils.ImageSource

@Composable
fun ImagePickerDialog(
    onSelect: (ImageSource) -> Unit
) {
    Column {

        Text(
            text = "Upload Image",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                onSelect(ImageSource.GALLERY)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Choose File")
        }
    }
}