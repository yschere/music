package com.example.music.ui.tooling

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "small-phone", device = "id:pixel_3a", apiLevel = 34)
@Preview(name = "phone", device = Devices.PIXEL, apiLevel = 34)
@Preview(name = "landscape", device = "spec:width=640dp,height=360dp,orientation=landscape,dpi=480", apiLevel = 34)
@Preview(name = "foldable", device = Devices.PIXEL_FOLD, apiLevel = 34)
@Preview(name = "tablet", device = Devices.PIXEL_TABLET, apiLevel = 34)
annotation class DevicePreviews
