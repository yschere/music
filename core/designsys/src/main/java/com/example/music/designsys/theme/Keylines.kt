package com.example.music.designsys.theme

import androidx.compose.ui.unit.dp

// KeyLines
val KEY_LINE_1 = 16.dp

// Image size values
val ITEM_IMAGE_CARD_SIZE = 160.dp
val ITEM_IMAGE_ROW_SIZE = 56.dp

// Padding values
val SMALL_PADDING = 4.dp
val CONTENT_PADDING = 8.dp
val DEFAULT_PADDING = 12.dp // original horizontal padding in app
val MARGIN_PADDING = 16.dp
val SCREEN_PADDING = 16.dp
val LIBRARY_TAB_PADDING = 24.dp
val MODAL_CONTENT_PADDING = 24.dp

// Icon and Button size values
val FAB_SIZE = 56.dp
val ICON_SIZE = 56.dp
val PRIMARY_BUTTON_SIZE = 72.dp
val SIDE_BUTTON_SIZE = 48.dp
val MINI_PLAYER_BUTTON_SIZE = 48.dp
val SCROLL_FAB_BOTTOM_PADDING = 48.dp

// Content margin size values
val CONTENT_LEFT_MARGIN = 72.dp
val ICON_BUTTON_RIGHT_MARGIN = 32.dp
val LARGE_IMAGE_HEIGHT = 340.dp // is for TopAppBar with large art cover // album details header only
val LIST_ITEM_HEIGHT = 56.dp // default is 72.dp but this so big
val MINI_PLAYER_HEIGHT = 72.dp
val ROW_ITEM_HEIGHT = 64.dp
val SUBTITLE_HEIGHT = 48.dp
val TOP_BAR_HEIGHT = 60.dp // also tool bar height
val TOP_BAR_COLLAPSED_HEIGHT = 60.dp // originally 48.dp
val TOP_BAR_EXPANDED_HEIGHT = 132.dp // TopAppBar with title/name only
val TOP_BAR_IMAGE_EXPANDED_HEIGHT = TOP_BAR_COLLAPSED_HEIGHT + ITEM_IMAGE_CARD_SIZE // collapsed top bar + item image
// card size art and title side by side <-> covers album and playlist details headers
// originally TopAppBarDefaults.LargeAppBarExpandedHeight(152) + 56.dp,
// large image dimension: 3:2

// Navigation Drawer size values
val NAV_DRAWER_CONTENT_LEFT_MARGIN = 72.dp // if has icon or avatar
val NAV_DRAWER_CONTENT_PADDING = 8.dp // between different content sections
val NAV_DRAWER_ITEMS_HEIGHT = 48.dp
val NAV_DRAWER_MARGINS = 16.dp // or 20.dp
val NAV_DRAWER_WIDTH = 240.dp
// large image dimension: 16:9
