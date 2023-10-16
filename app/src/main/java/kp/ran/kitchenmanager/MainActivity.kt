package kp.ran.kitchenmanager

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kp.ran.kitchenmanager.ui.theme.KitchenManagerTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        val db = Firebase.firestore
         splashScreen.apply {
            setKeepOnScreenCondition {

                viewModel.isLoading.value
            }
        }
        setContent {
            KitchenManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                ///  HomeScreen()
                    Greeting(applicationContext,db)
                }
            }
        }
    }
}

@Composable
fun Greeting(context: Context, db: FirebaseFirestore) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController)
        }

        composable("WhoisCooking") {
            WhoisCooking(navController)
        }

        composable("iamcooking"){
            IamCookingScreen(db)
        }
        composable("FoodDetails"){
            FoodDetails(context = context)
        }

//        composable("detail/{itemId}/{itemId2}/{abc}/{hi}") {
//                backStackEntry ->
//            val itemId = backStackEntry.arguments?.getString("itemId")
//            val itemId2 = backStackEntry.arguments?.getString("itemId2")
//            val s1 = backStackEntry.arguments?.getString("abc")
//         //   DetailScreen(itemId = itemId, itemId2 = itemId2, xyz = s1, hh = "some")
//        }
    }
}


@Composable
fun HomeScreen(navController: NavHostController) {

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { navController.navigate("iamcooking") }) {
            Text(text = "I want to cook")
        }
        Button(onClick = {
            navController.navigate("WhoisCooking")  }) {
            Text(text = "Who is cooking")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IamCookingScreen(db: FirebaseFirestore) {
    var ctime by remember {
        mutableStateOf("")
    }
    var building by remember {
        mutableStateOf("")
    }
    var floor by remember {
        mutableStateOf("")
    }
    var foodplan by remember {
        mutableStateOf("")
    }
    var asa by remember {
        mutableStateOf("")
    }
    var ad by remember {
        mutableStateOf("")
    }


    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(id = R.string.iamcooking))

        OutlinedTextField(value = ctime, onValueChange ={ctime=it}
            , placeholder = { Text(text = "Date & Time of Cooking")})
        OutlinedTextField(value = building, onValueChange ={building=it} , placeholder = { Text(text = "Building name")})
        OutlinedTextField(value = floor, onValueChange ={floor=it} , placeholder = { Text(text = "Floor")})
        OutlinedTextField(value = foodplan, onValueChange ={foodplan=it} , placeholder = { Text(text = "Food plan")})
        Button(onClick = {

            savedata("9876543210",db,ctime,building,floor,foodplan)

        }) {
            Text(text = "Save plan")
        }
    }
}

fun savedata(Mobnum:String,db: FirebaseFirestore, ctime: String, building: String, floor: String, foodplan: String) {
    // Create a new user with a first, middle, and last name


    val user = hashMapOf(
        "Mobnum" to Mobnum,
        "ctime"  to ctime,
        "building" to building,
        "floor" to floor,
        "foodplan" to foodplan
    )

// Add a new document with a generated ID

    db.collection(Mobnum)
        .add(user)
        .addOnSuccessListener {

            Log.d("abcd123d", "savedata: Success")

         }
        .addOnFailureListener {
                e ->
            Log.d("abcd123d", "savedata: failed ${e.message}")

         }
}

@Composable
fun WhoisCooking(navController: NavHostController) {

    var name by remember {
        mutableStateOf("")
    }
    var building by remember {
        mutableStateOf("")
    }
    var floor by remember {
        mutableStateOf("")
    }
    var foodplan by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        LazyColumn(){
            item {
             //   Text(text = "$name is cooking $foodplan at $building")

                foodList("Victor is cooking Dosa at Einstin building",navController)
            }
            item {
             //   Text(text = "$name is cooking $foodplan at $building")

                foodList( "Mark is cooking Idly at Thomas building",navController)
            }
            item {
             //   Text(text = "$name is cooking $foodplan at $building")
                foodList("John is cooking Meals at APJ building",navController)

            }
        }
    }
}

@Composable
fun foodList(title: String,navController: NavHostController) {
    val context = LocalContext.current

    Card (modifier = Modifier.padding(5.dp)){


        Text(
            text = title,
            fontSize = 22.sp,
            color = Color.Blue,
            modifier = Modifier
                .fillMaxWidth()

                .clickable {
                    navController.navigate("FoodDetails")
                },
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetails(context:Context){
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        var msg by remember { mutableStateOf("") }
        var tf by remember { mutableStateOf(false) }

        Text(text = "John is cooking Meals at APJ building")
        Button(onClick = {
            tf=!tf
        }) {
            Text(text = "Contact John")
        }
        if (tf) {
            OutlinedTextField(value = msg, onValueChange = { msg = it })

            Button(onClick = {
                Toast
                    .makeText(context, "message sent!", Toast.LENGTH_LONG)
                    .show()
            }) {
                Text(text = "Send Message")
            }
        }
    }
}