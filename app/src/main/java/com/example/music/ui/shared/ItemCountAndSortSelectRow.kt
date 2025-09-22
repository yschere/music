package com.example.music.ui.shared

import androidx.annotation.PluralsRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.music.R
import com.example.music.util.MultiSelectBtn
import com.example.music.util.SortBtn
import com.example.music.util.quantityStringResource

/**
 * Content section: Item Count and Sort, Select buttons
 */
@Composable
fun ItemCountAndSortSelectButtons(
    @PluralsRes id: Int,
    itemCount: Int,
    onSortClick: () -> Unit,
    onSelectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        // Item Count
        Text(
            text = """\s[a-z]""".toRegex().replace(
                quantityStringResource(id, itemCount, itemCount)
            ) {
                it.value.uppercase()
            },
            textAlign = TextAlign.Left,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp).weight(1f, true)
        )

        // Sort btn
        SortBtn(onClick = onSortClick)

        // Multi-Select btn
        MultiSelectBtn(onClick = onSelectClick)
    }
}
