package com.example.ordme.ui.screen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.ordme.R
import com.example.ordme.base.BaseFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseFragment() {
    override val layout: Int = R.layout.fragment_login

    private companion object {
        private const val GOOGLE_SIGN_IN = 9001
        private const val RESULT_CANCELD = 1
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private val fbAuth = FirebaseAuth.getInstance()
    private val fbUser = fbAuth.currentUser
    private val db = FirebaseFirestore.getInstance()

    override fun subscribeUi() {

        //we check that your account isn't null and your email verified
        if (fbUser != null && fbUser.isEmailVerified) {
            findNavController().navigate(R.id.action_loginUserFragment_to_mainUserFragment)
        }

        registerButtton.setOnClickListener {
            findNavController().navigate(R.id.action_loginUserFragment_to_registerUserFragment)
        }

        loginClick()
        googleSignIN()
    }

    private fun loginClick() {
        loginBT.setOnClickListener {
            val email = emailET.text.toString()
            val password = passwordET.text.toString()

            if (email == "" || password == "") {
                return@setOnClickListener
            } else {

                //we check that this data is in our datebase
                fbAuth.signInWithEmailAndPassword(
                    email,
                    password
                )
                    .addOnSuccessListener { authRes ->
                        if (authRes != null) {

                            //before your login we check that you verified your email
                            if (fbAuth.currentUser!!.isEmailVerified) {
                                findNavController().navigate(R.id.action_loginUserFragment_to_mainUserFragment)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please verify your email.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Snackbar.make(
                            requireView(),
                            "Your account is not exist.",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                        Log.d("DEBUG", exception.message.toString())
                    }
            }
        }
    }

    private fun googleSignIN() {

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        //sign in google account
        googleSignIn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
        }
    }

//    override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = fbAuth.currentUser
//        updateUI(currentUser)
//    }

    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            Log.w("lipa", "Google sign in failed")
            return
        } else {
            findNavController().navigate(R.id.action_loginUserFragment_to_mainUserFragment)

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val excepction = task.exception

            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("SigInGoogle", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("SigInGoogle", "Google sign in failed", e)
                }
            } else {
                Log.w("SigInGoogle", excepction.toString())
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        fbAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Great!!!", "signInWithCredential:success")
                    findNavController().navigate(R.id.action_loginUserFragment_to_mainUserFragment)
                    addUserToFirebase()
                    //val user = fbAuth.currentUser
                    //updateUI(user)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("There is something bad", "signInWithCredential:failure", task.exception)
                    //updateUI(null)

                }
            }
    }

    private fun addUserToFirebase() {
        val currentUser = fbAuth.currentUser

        val dataUser = hashMapOf(
            "email" to currentUser?.email,
            "uid" to currentUser?.uid,
            "full_name" to currentUser?.displayName,
            "number" to currentUser?.phoneNumber,
            "photo" to currentUser?.photoUrl
        )

        db.collection("users")
            .document(currentUser!!.uid)
            .set(dataUser)

        Log.d("sssssss", currentUser.uid)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.login_restaurant_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.loginRestaurantAction -> {
            }
        }
        return false
    }

    override fun unsubscribeUi() {

    }
}