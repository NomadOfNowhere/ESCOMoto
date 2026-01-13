package com.ipn.escomoto.ui.common

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

@Composable
fun ImgFromString(
    imageName: String,
    contentDescription: String = "",
    modifier: Modifier = Modifier,
    onImageNotFound: @Composable () -> Unit
) {
    val context = LocalContext.current

    val resId = remember(imageName) {
        context.resources.getIdentifier(
            imageName,
            "drawable",
            context.packageName
        )
    }

    if (resId != 0) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = contentDescription,
            modifier = modifier
        )
    }
    else {
        onImageNotFound()
    }
}