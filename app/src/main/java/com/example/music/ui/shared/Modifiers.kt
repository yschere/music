package com.example.music.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.example.music.designsys.theme.CONTENT_PADDING
import com.example.music.designsys.theme.DEFAULT_PADDING
import com.example.music.designsys.theme.MODAL_CONTENT_PADDING
import com.example.music.designsys.theme.SCREEN_PADDING
import com.example.music.designsys.theme.SMALL_PADDING

/**
 * Standardized modifier for text padding
 */
fun Modifier.frontTextPadding() =
    this.padding(start = CONTENT_PADDING)

/**
 * Standardized modifier for defining subtitle text height padding
 */
fun Modifier.heightPadding() =
    this.padding(vertical = SMALL_PADDING)

/**
 * Standardized modifier for list items in row form
 */
fun Modifier.itemRowPadding() =
    this.padding(vertical = CONTENT_PADDING)
        .padding(start = CONTENT_PADDING)

/**
 * Standardized modifier for list item images and icons
 * @param size defines height x width of the icon
 * @param shape defines the corner shape of the icon
 */
fun Modifier.listItemIconMod(size: Dp, shape: CornerBasedShape) =
    this.size(size).clip(shape)

/**
 * Standardized modifier for Bottom Sheet Modal content
 */
fun Modifier.modalPadding() =
    this.padding(horizontal = MODAL_CONTENT_PADDING)

/**
 * Standardized modifier for Bottom Sheet Modal header content
 */
fun Modifier.modalHeaderPadding() =
    this.padding(horizontal = MODAL_CONTENT_PADDING, vertical = DEFAULT_PADDING)

/**
 * Standardized modifier for Player Buttons
 * @param size defines height x width of the icon in the button
 * @param color defines the background color of the button
 */
fun Modifier.playerButtonMod(size: Dp, color: Color) =
    this.size(size)
        .background(color = color, shape = CircleShape)
        .semantics { role = Role.Button }

/**
 * Standardized modifier for screen margins
 */
fun Modifier.screenMargin() =
    this.padding(horizontal = SCREEN_PADDING)

/**
 * Standardized modifier for song count sticker on item cards
 * @param color defines the background color for the sticker
 */
fun Modifier.songCountCard(color: Color) =
    this.padding(CONTENT_PADDING)
        .background(color = color, shape = CircleShape)
        .padding(SMALL_PADDING)
