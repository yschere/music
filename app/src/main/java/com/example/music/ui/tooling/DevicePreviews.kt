/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.music.ui.tooling

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "small-phone", device = "id:pixel_3a", apiLevel = 34)
@Preview(name = "phone", device = Devices.PIXEL, apiLevel = 34)
@Preview(name = "landscape", device = "spec:width=640dp,height=360dp,orientation=landscape,dpi=480",
    apiLevel = 34
)
@Preview(name = "foldable", device = Devices.PIXEL_FOLD, apiLevel = 34)
@Preview(name = "tablet", device = Devices.PIXEL_TABLET, apiLevel = 34)
annotation class DevicePreviews
