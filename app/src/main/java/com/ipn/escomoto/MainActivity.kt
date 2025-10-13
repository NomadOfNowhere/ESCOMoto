package com.ipn.escomoto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ipn.escomoto.ui.theme.ESCOMotoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ESCOMotoTheme {
                WelcomeScreen(
                    onCreateAccountClick = {
                        // Navegar a pantalla de registro
                        // TODO: Implementar navegación
                    },
                    onLoginClick = {
                        // Navegar a pantalla de login
                        // TODO: Implementar navegación
                    }
                )
            }
        }
    }
}

@Composable
fun WelcomeScreen(
    onCreateAccountClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A3E),
                        Color(0xFF0A0A1F)
                    )
                )
            )
    ) {
        // Fondo SVG completo (opcional - si quieres usarlo)
        // Image(
        //     painter = painterResource(id = R.drawable.wave_background),
        //     contentDescription = "Background",
        //     modifier = Modifier.fillMaxSize(),
        //     contentScale = ContentScale.FillBounds
        // )

        // Decoración ondulada superior
        WaveDecoration(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(120.dp),
            isTop = true
        )

        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Logo ESCOM superior izquierda
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                LogoPlaceholder(
                    text = "ESCOM",
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Logo central circular
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF9B88FF),
                                Color(0xFF7B68EE),
                                Color(0xFF6B58DE)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Ícono de motocicleta
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_dialog_info), // Temporal
                        contentDescription = "Logo Pesconotorio",
                        modifier = Modifier.size(100.dp),
                        tint = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pesconotorio",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Logo IPN derecha (superpuesto)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = 100.dp, y = (-140).dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                LogoPlaceholder(
                    text = "IPN",
                    modifier = Modifier.size(70.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Título principal
            Text(
                text = "¡El acceso nunca\nfue tan rápido!",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 38.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtítulo
            Text(
                text = "Sistema de Control de Acceso para\nMotocicletas en ESCOM",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón Crear Cuenta
            Button(
                onClick = onCreateAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7B68EE)
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "CREAR CUENTA",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Texto de inicio de sesión
            Row(
                modifier = Modifier.clickable { onLoginClick() },
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¿Ya tienes cuenta? ",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "INICIA SESIÓN",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Decoración ondulada inferior
        WaveDecoration(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(100.dp),
            isTop = false
        )
    }
}

@Composable
fun WaveDecoration(
    modifier: Modifier = Modifier,
    isTop: Boolean
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val path = androidx.compose.ui.graphics.Path().apply {
            if (isTop) {
                // Onda superior
                moveTo(0f, 0f)
                lineTo(width, 0f)
                lineTo(width, height * 0.6f)

                // Curva ondulada
                cubicTo(
                    width * 0.75f, height * 0.9f,
                    width * 0.5f, height * 0.5f,
                    width * 0.25f, height * 0.8f
                )
                cubicTo(
                    width * 0.15f, height * 0.9f,
                    0f, height * 0.7f,
                    0f, height * 0.5f
                )
                close()
            } else {
                // Onda inferior
                moveTo(0f, height)
                lineTo(width, height)
                lineTo(width, height * 0.4f)

                // Curva ondulada
                cubicTo(
                    width * 0.75f, height * 0.1f,
                    width * 0.5f, height * 0.5f,
                    width * 0.25f, height * 0.2f
                )
                cubicTo(
                    width * 0.15f, height * 0.1f,
                    0f, height * 0.3f,
                    0f, height * 0.5f
                )
                close()
            }
        }

        drawPath(
            path = path,
            color = Color(0xFF7B68EE).copy(alpha = 0.3f)
        )

        // Borde de la onda
        drawPath(
            path = path,
            color = Color(0xFF7B68EE),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
        )
    }
}

@Composable
fun LogoPlaceholder(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = Color(0xFF7B68EE).copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    ESCOMotoTheme {
        WelcomeScreen()
    }
}
// adb install -r C:\Users\Joshua\AndroidStudioProjects\ESCOMoto\app\build\outputs\apk\debug\app-debug.apk