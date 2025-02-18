package com.example.music.ui.shared

import androidx.compose.material3.Button
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

//@Composable
//fun MoreOptionsBottomModal(
//    showBottomSheet: Boolean,
////    sheetState: SheetState,
//
//) {
//    ModalBottomSheet(
//        onDismissRequest = {
//            showBottomSheet = false
//        },
//        sheetState = sheetState
//    ) {
//        // Sheet content
//        Button(onClick = {
//            scope.launch { sheetState.hide() }.invokeOnCompletion {
//                if (!sheetState.isVisible) {
//                    showBottomSheet = false
//                }
//            }
//        }) {
//            Text("Hide bottom sheet")
//        }
//    }
//}