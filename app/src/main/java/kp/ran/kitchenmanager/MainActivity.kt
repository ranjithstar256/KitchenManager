package kp.ran.kitchenmanager

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import kp.ran.kitchenmanager.ui.theme.KitchenManagerTheme
import java.util.*

class MainActivity : ComponentActivity() {
    private val viewModell: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        splashScreen.apply {
            setKeepOnScreenCondition {
                viewModell.isLoading.value
            }
        }

        setContent {
            KitchenManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    // Get the current user's ID
               //     val currentUserId = Firebase.auth.currentUser?.uid
               //     Toast.makeText(applicationContext,currentUserId,Toast.LENGTH_LONG).show()
               //     val currentUserId: String? = currentUser?.uid

                  //  currentUserId.let {
                 //       Toast.makeText(applicationContext,currentUserId,Toast.LENGTH_LONG).show()

    //                }

                    val mobNum = intent.getStringExtra("mobnumber")
                    val currentUserid = intent.getStringExtra("userid")

                    val cookingInfoViewModel: CookingInfoViewModel = viewModel()
                    Greeting(applicationContext,cookingInfoViewModel,
                        mobNum.toString(),currentUserid.toString())
                }
            }
        }
    }
}

@Composable
fun Greeting(
    context: Context,
    cookingInfoViewModel: CookingInfoViewModel,
    mobNum: String,
    currentUserid: String
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController)
        }

        composable("WhoisCooking") {
            CookingList(navController,mobNum,currentUserid)
        }

        composable("iamcooking"){
           // IamCookingScreen(db)
            //if (currentUserId != null) {
                CookingInfoInput(context, mobNum.toString(),currentUserid) {
                        cookingInfo ->
                    cookingInfoViewModel.saveCookingInfo(cookingInfo)
            //    }
            }
        }

        composable(
            "cookingDetail/{cookingInfo}",
            arguments = listOf(navArgument("cookingInfo") { type = NavType.StringType })
        ) { backStackEntry ->
            val cookingInfoJson = backStackEntry.arguments?.getString("cookingInfo")
            val cookingInfo = Gson().fromJson(cookingInfoJson, CookingInfo::class.java)
            CookingDetailScreen(navController, cookingInfo,currentUserid,context)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookingDetailScreen(
    navController: NavController,
    cookingInfo: CookingInfo,
    currentUserid: String,
    context: Context
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Cooking Date & Time: ${cookingInfo.cookName}")
        Text("Cooking Date & Time: ${cookingInfo.cookingDateTime}")
        Text("Mobile Number: ${cookingInfo.cookMobileNumber}")
        Text("Cooking Building: ${cookingInfo.cookingBuilding}")
        Text("Floor Number: ${cookingInfo.floorNumber}")
        Text("Cooking Item: ${cookingInfo.cookingItem}")
        var tf by remember { mutableStateOf(false) }
        var msg by remember { mutableStateOf("") }

        Button(onClick = {   tf=!tf }) {
            Text(text = "Contact ${cookingInfo.cookName}")
        }

        if (tf) {
            OutlinedTextField(value = msg, onValueChange = { msg = it })
            Button(onClick = {
                Log.d("fjbjfk898", "ReaderId $currentUserid  \n Cookid: "+cookingInfo.cookUserId)


                // Send a message
                val message = MessageFeature(currentUserid , cookingInfo.cookUserId, msg, System.currentTimeMillis())

                val ss = CookingInfoViewModel()
                if (ss.sendmessage(message)){
                    Toast.makeText(context,"Message sent successfully",Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context,"Message sending failed, please retry ",Toast.LENGTH_LONG).show()

                }

            }) {
                Text(text = "Send Message")
            }
        }

        Button(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("Go Back")
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController) {

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally)
    {
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
fun CookingInfoInput(
    context: Context,
    mobNum: String,
    currentUserid: String,
    onSaveClick: (CookingInfo) -> Unit
) {
     var cookingName by remember { mutableStateOf("") }
    var cookingDateTime by remember { mutableStateOf("") }
    var cookingBuilding by remember { mutableStateOf("") }
    var floorNumber by remember { mutableStateOf("") }
    var cookingItem by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally)
    {
        OutlinedTextField(
            value = cookingName,
            onValueChange = {

                cookingName = it },
            placeholder = { Text("Name - Who is Cooking") }
        )
        OutlinedTextField(
            value = cookingDateTime,
            onValueChange = {

                cookingDateTime = it },
            placeholder = { Text("Cooking Date & Time") }
        )

      /*  OutlinedTextField(
            value = cookMobileNumber,
            onValueChange = { cookMobileNumber = it },
            placeholder = { Text("Cook Mobile Number") }
        )*/

        OutlinedTextField(
            value = cookingBuilding,
            onValueChange = { cookingBuilding = it },
            placeholder = { Text("Cooking Building") }
        )

        OutlinedTextField(
            value = floorNumber,
            onValueChange = { floorNumber = it },
            placeholder = { Text("Floor Number") }
        )

        OutlinedTextField(
            value = cookingItem,
            onValueChange = { cookingItem = it },
            placeholder = { Text("Cooking Item") }
        )

        Button(
            onClick = {
                val info =
                    CookingInfo(
                        currentUserid,
                        cookingName,
                        cookingDateTime,
                        mobNum,
                        cookingBuilding,
                        floorNumber,
                        cookingItem
                    )

                if (info != null) {
                    onSaveClick(info)
                }
                cookingName=""
                cookingDateTime=""
                cookingBuilding=""
                floorNumber=""
                cookingItem=""
                Toast.makeText(context,"data saved !",Toast.LENGTH_LONG).show()
            }
        ) {
            Text("Save")
        }
    }
}

@Composable
fun CookingList(navController: NavController, mobNum: String?, currentUserid: String?) {
    val viewModel: CookingInfoViewModel = viewModel()
    val cookingInfoList = viewModel.getCookingInfoList().collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(cookingInfoList.value) {
                cookingInfo ->
            CookingListItem(cookingInfo, navController,mobNum,currentUserid)
        }
    }
}

@Composable
fun CookingListItem(
    cookingInfo: CookingInfo, navController:
    NavController, mobNum: String?, currentUserid: String?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val cookingInfoJson = Gson().toJson(cookingInfo)
                navController.navigate("cookingDetail/$cookingInfoJson")
            }
            .padding(8.dp),

        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally)
         {
            Text(text = "Cooking Date & Time: ${cookingInfo.cookName}")
            Text(text = "Cooking Date & Time: $mobNum")
            Text(text = "Mobile Number: ${cookingInfo.cookMobileNumber}")
            Text(text = "Cooking Building: ${cookingInfo.cookingBuilding}")
            Text(text = "Floor Number: ${cookingInfo.floorNumber}")
            Text(text = "Cooking Item: ${cookingInfo.cookingItem}")
        }
    }
}


/*
@Composable
fun DateTimePickerTextField() {
    var isDialogOpen by remember { mutableStateOf(false) }
    var selectedDateTime by remember { mutableStateOf(Calendar.getInstance()) }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    Column {
        BasicTextField(
            value = dateFormat.format(selectedDateTime.time),
            onValueChange = { */
/* Do nothing, read-only *//*
 },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isDialogOpen = true },
            textStyle = TextStyle(fontSize = 20.sp),
            readOnly = true
        )

        if (isDialogOpen) {
            DateTimePickerDialog(
                initialDateTime = selectedDateTime,
                onDateTimeSelected = {
                    selectedDateTime = it
                    isDialogOpen = false
                }
            )
        }
    }
}

@Composable
fun DateTimePickerDialog(
    initialDateTime: Calendar,
    onDateTimeSelected: (Calendar) -> Unit
) {
    var dateTimePickerState by remember { mutableStateOf(initialDateTime) }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    BackHandler {
        // Handle back button press to close the dialog
        onDateTimeSelected(dateTimePickerState)
    }

    AlertDialog(
        onDismissRequest = { onDateTimeSelected(dateTimePickerState) },
        title = { Text("Select Date and Time") },
        text = {
            Column {
                DateTimePickerDialog(initialDateTime = dateTimePickerState, onDateTimeSelected = {
                    dateTimePickerState=it
                })

                Spacer(modifier = Modifier.height(16.dp))

            }
        },
        confirmButton = {
            Button(
                onClick = { onDateTimeSelected(dateTimePickerState) }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDateTimeSelected(initialDateTime) }
            ) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    readOnly: Boolean = false
) {
    val textState = remember { mutableStateOf(TextFieldValue(value)) }
    if (!readOnly) {
        textState.value = TextFieldValue(value)
        TextField(
            value = textState.value,
            onValueChange = {
                textState.value = it
                onValueChange(it.text)
            },
            textStyle = textStyle,
            modifier = modifier,
            readOnly = readOnly
        )
    } else {
        Text(
            textState.value.text,
            style = textStyle,
            modifier = modifier
                .background(Color.Transparent)
                .clickable { */
/* No-op for read-only *//*
 }
        )
    }
}
*/
/*
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

            savedata("9987654321",db,ctime,building,floor,foodplan)

        }) {
            Text(text = "Save plan")
        }
    }
}
*/
/*
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

            Log.d("abcd123dzz", "inde Success")

         }
        .addOnFailureListener {
                e ->
            Log.d("abcd123dxx", " failed ${e.message}")

         }
}
*/
/*@Composable
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
        CookingList()
    }
}*/
/*
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
*/
/*
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

*/