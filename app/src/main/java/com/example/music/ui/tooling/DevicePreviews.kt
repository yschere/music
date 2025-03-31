package com.example.music.ui.tooling

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "small-phone", device = "id:pixel_3a", apiLevel = 34)
@Preview(name = "phone", device = Devices.PIXEL, apiLevel = 34)
@Preview(name = "landscape", device = "spec:width=640dp,height=360dp,orientation=landscape,dpi=480", apiLevel = 34)
@Preview(name = "foldable", device = Devices.PIXEL_FOLD, apiLevel = 34)
@Preview(name = "tablet", device = Devices.PIXEL_TABLET, apiLevel = 34)
annotation class DevicePreviews

@Preview( name="system-light-view", showBackground=true,
    device=Devices.PIXEL, showSystemUi=true,
    uiMode=Configuration.UI_MODE_NIGHT_NO, apiLevel=34 )
annotation class SystemLightPreview

@Preview( name="system-dark-view", showBackground=true,
    device=Devices.PIXEL, showSystemUi=true,
    uiMode=Configuration.UI_MODE_NIGHT_YES , apiLevel=34)
annotation class SystemDarkPreview

@Preview( name = "component-light-mode", showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO, apiLevel = 34 )
annotation class CompLightPreview

@Preview( name = "component-dark-mode", showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES, apiLevel = 34 )
annotation class CompDarkPreview

@Preview( name = "landscape-view", showBackground = true,
    device = "spec:width=780dp,height=360dp,orientation=landscape,dpi=480",
    uiMode = Configuration.UI_MODE_NIGHT_YES, apiLevel = 34 )
annotation class LandscapePreview