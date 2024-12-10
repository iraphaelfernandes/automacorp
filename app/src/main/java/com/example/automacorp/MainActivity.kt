package com.example.automacorp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.automacorp.services.RoomViewModel
import com.example.automacorp.ui.theme.AutomacorpTheme
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {
    private val roomViewModel: RoomViewModel by viewModels()

    companion object {
        const val ROOM_PARAM = "com.automacorp.room.attribute"
    }


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onSayHelloButtonClick: (name: String) -> Unit = { name ->
            val validRoomPattern = Regex("^r.*", RegexOption.IGNORE_CASE)
            val intent = if (validRoomPattern.matches(name)) {
                Intent(this, RoomActivity::class.java).apply {
                    putExtra(ROOM_PARAM, name)
                }
            } else {
                Intent(this, NotFoundActivity::class.java)
            }
            startActivity(intent)
        }


        setContent {
            AutomacorpTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        onClick = onSayHelloButtonClick,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}


@Composable
fun AppLogo(modifier: Modifier) {
    Image(
        painter = painterResource(R.drawable.ic_logo),
        contentDescription = stringResource(R.string.app_logo_description),
        modifier = modifier.paddingFromBaseline(top = 100.dp).height(80.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting(onClick: (name: String) -> Unit, modifier: Modifier = Modifier) {

    Column {
        AppLogo(Modifier.padding(top = 32.dp).fillMaxWidth())
        Text(
            stringResource(R.string.act_main_welcome),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
        var name by remember { mutableStateOf("") }

        OutlinedTextField(
            name,
            onValueChange = { name = it },
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            placeholder = {
                Row {
                    Icon(
                        Icons.Rounded.AccountCircle,
                        modifier = Modifier.padding(end = 8.dp),
                        contentDescription = stringResource(R.string.act_main_fill_name),
                    )
                    Text(stringResource(R.string.act_main_fill_name))
                }
            })

        Button(
            onClick = { onClick(name) },
            modifier = Modifier.padding(8.dp).align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.act_main_open))
        }
    }
}
