import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.automacorp.R
import com.example.automacorp.RoomActivity
import com.example.automacorp.ui.theme.AutomacorpTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AutomacorpTopAppBar(title: String? = null, returnAction: () -> Unit = {}) {
    val context = LocalContext.current
    val appBarColors = TopAppBarDefaults.mediumTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    )

    val navigationActions: @Composable RowScope.() -> Unit = {
        IconButton(onClick = { navigateToRoom(context) }) {
            Icon(
                painter = painterResource(R.drawable.ic_action_rooms),
                contentDescription = stringResource(R.string.app_go_room_description)
            )
        }
        IconButton(onClick = { handleContactAction(context, ContactType.EMAIL) }) {
            Icon(
                painter = painterResource(R.drawable.ic_action_mail),
                contentDescription = stringResource(R.string.app_go_mail_description)
            )
        }
        IconButton(onClick = { handleContactAction(context, ContactType.GITHUB) }) {
            Icon(
                painter = painterResource(R.drawable.ic_action_github),
                contentDescription = stringResource(R.string.app_go_github_description)
            )
        }
    }

    if (title == null) {
        TopAppBar(
            title = { Text("") },
            colors = appBarColors,
            actions = navigationActions
        )
    } else {
        MediumTopAppBar(
            title = { Text(title) },
            colors = appBarColors,
            navigationIcon = {
                IconButton(onClick = returnAction) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.app_go_back_description)
                    )
                }
            },
            actions = navigationActions
        )
    }
}

private enum class ContactType {
    EMAIL, GITHUB
}

private fun navigateToRoom(context: Context) {
    val intent = Intent(context, RoomActivity::class.java)
    context.startActivity(intent)
    (context as? Activity)?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}

private fun handleContactAction(context: Context, type: ContactType) {
    try {
        val intent = when (type) {
            ContactType.EMAIL -> createEmailIntent()
            ContactType.GITHUB -> createGithubIntent()
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun createEmailIntent() = Intent(Intent.ACTION_SENDTO).apply {
    data = Uri.parse("mailto:raphael.fernandes.eng@gmail.com")
    putExtra(Intent.EXTRA_SUBJECT, "Contact Us")
    putExtra(Intent.EXTRA_TEXT, "Hello, I have an inquiry about your app.")
}

private fun createGithubIntent() = Intent(Intent.ACTION_VIEW).apply {
    data = Uri.parse("https://github.com/iraphaelfernandes")
}

@Preview(showBackground = true)
@Composable
fun AutomacorpTopAppBarHomePreview() {
    AutomacorpTheme {
        AutomacorpTopAppBar(null)
    }
}

@Preview(showBackground = true)
@Composable
fun AutomacorpTopAppBarPreview() {
    AutomacorpTheme {
        AutomacorpTopAppBar("A page")
    }
}