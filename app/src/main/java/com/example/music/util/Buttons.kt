/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.music.util

//@Composable
//fun ToggleFollowPodcastIconButton(
//    isFollowed: Boolean,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val clickLabel = stringResource(if (isFollowed) R.string.cd_unfollow else R.string.cd_follow)
//    IconButton(
//        onClick = onClick,
//        modifier = modifier.semantics {
//            onClick(label = clickLabel, action = null)
//        }
//    ) {
//        Icon(
//            // TODO: think about animating these icons
//            imageVector = when {
//                isFollowed -> Icons.Default.Check
//                else -> Icons.Default.Add
//            },
//            contentDescription = when {
//                isFollowed -> stringResource(R.string.cd_following)
//                else -> stringResource(R.string.cd_not_following)
//            },
//            tint = animateColorAsState(
//                when {
//                    isFollowed -> MaterialTheme.colorScheme.onPrimary
//                    else -> MaterialTheme.colorScheme.primary
//                }
//            ).value,
//            modifier = Modifier
//                .shadow(
//                    elevation = animateDpAsState(if (isFollowed) 0.dp else 1.dp).value,
//                    shape = MaterialTheme.shapes.small
//                )
//                .background(
//                    color = animateColorAsState(
//                        when {
//                            isFollowed -> MaterialTheme.colorScheme.primary
//                            else -> MaterialTheme.colorScheme.surfaceContainerHighest
//                        }
//                    ).value,
//                    shape = CircleShape
//                )
//                .padding(4.dp)
//        )
//    }
//}


//@Composable
//fun SmallFloatingActionButton(
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier,
//    shape: Shape = FloatingActionButtonDefaults.smallShape,
//    containerColor: Color = FloatingActionButtonDefaults.containerColor,
//    contentColor: Color = contentColorFor(containerColor),
//    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
//    interactionSource: MutableInteractionSource? = null,
//    content: @Composable () -> Unit
//) {
//    Icon(
//        Icons.Filled.KeyboardDoubleArrowUp,
//        //Icons.Filled.VerticalAlignTop,
//        contentDescription = "Jump to top of screen",
//        modifier = modifier.clip(shape).size(),
//        //modifier = Modifier.size(FloatingActionButtonDefaults.SmallIconSize),
//    )
//}


/* //likely the section where the Floating Action Button is placed in a Composable
val listState = rememberLazyListState()
// The FAB is initially shown. Upon scrolling past the first item we hide the FAB by using a
// remembered derived state to minimize unnecessary compositions.
val fabVisible by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

Scaffold(
floatingActionButton = {
    SmallFloatingActionButton(
        modifier =
        Modifier.animateFloatingActionButton(
            visible = fabVisible,
            alignment = Alignment.BottomEnd
        ),
        onClick = { /* do something */ },
    ) {
        Icon(
            Icons.Filled.KeyboardDoubleArrowUp,
            //Icons.Filled.VerticalAlignTop,
            contentDescription = "Jump to top of screen",
            modifier = modifier.clip(shape).size(),
            //modifier = Modifier.size(FloatingActionButtonDefaults.SmallIconSize),
        )
    }
},
floatingActionButtonPosition = FabPosition.End,
) {
    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        for (index in 0 until 100) {
            item { Text(text = "List item - $index", modifier = Modifier.padding(24.dp)) }
        }
    }
}*/

/*
//visibility logic for button that is supposed to be visible once not at top of the list
AnimatedVisibility(visible = !isAtTopOfList) {
        ScrollToTopButton()
    }
 */