package app.xswallet.ui.pages.settings.screens

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import app.xswallet.R
import app.xswallet.ui.AppStrings
import app.xswallet.BuildConfig

val time = BuildConfig.BUILD_TIME
@Composable
fun AboutScreen(
    onBack: () -> Unit,
    strings: AppStrings
) {
    val context = LocalContext.current
    var appIconPainter by remember { mutableStateOf<BitmapPainter?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val drawable = context.packageManager.getApplicationIcon(context.packageName)
            if (drawable is BitmapDrawable) {
                appIconPainter = BitmapPainter(drawable.bitmap.asImageBitmap())
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = strings.back
                )
            }
            Text(
                text = "关于&帮助",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (appIconPainter != null) {
                    Image(
                        painter = appIconPainter!!,
                        contentDescription = "应用图标",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "版本号 ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "构建日期(UTC) ${BuildConfig.BUILD_TIME}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "构建类型 Alpha",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .height(200.dp)
                    .align(Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "开发人员",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "服务端",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.about_jg),
                        contentDescription = "杰哥",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "杰哥",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "客户端",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.about_xl),
                        contentDescription = "二阶堂 希罗",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "二阶堂 希罗",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Q&A",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Q: 如果我发现了软件的一个问题，我该如何反馈？\nA: 发现问题当然欢迎反馈，只需要完整的写下你当时干了什么导致了这个问题，并提交到师太手中即可，还可以领取学士币（多少就不知道了QwQ）",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Q: 如果我有编程能力，可以加入开发吗？\nA: 当然可以，毕竟我们整个开发组就俩人，实在是要多几个帮手，你可以选择加入前端app开发，也可以加入后端开发，同样的，把《入职申请》（是叫这个吧？）交给师太即可",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "暂时只有这些，如果大家有问题需要这里统一解答的，告诉师太即可（三遍了）",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}