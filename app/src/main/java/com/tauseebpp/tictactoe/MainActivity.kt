package com.tauseebpp.tictactoe

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var database=FirebaseDatabase.getInstance()
    private var myRef=database.reference

    var myEmail:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        var b:Bundle=intent.extras!!
        myEmail=b.getString("email")
        incomingCalls()

    }
        fun buclick(view: View){
            val buSelected :Button = view as Button
            var cellId=0
            when(buSelected.id){
                R.id.bu1 -> cellId = 1
                R.id.bu2 -> cellId = 2
                R.id.bu3 -> cellId = 3
                R.id.bu4 -> cellId = 4
                R.id.bu5 -> cellId = 5
                R.id.bu6 -> cellId = 6
                R.id.bu7 -> cellId = 7
                R.id.bu8 -> cellId = 8
                R.id.bu9 -> cellId = 9
            }

            //Log.d("buclick :",buSelected.id.toString())
            //Log.d("buclick : cellId",cellId.toString())
            //playgame(cellId, buSelected)

            myRef.child("PlayerOnline").child(sessionID!!).child(cellId.toString()).setValue(myEmail)
        }

    var activeplayer=1
    var player1 = ArrayList<Int>()
    var player2 = ArrayList<Int>()

    fun playgame(cellId: Int, buselected: Button){

        if(activeplayer == 1)
        {
            buselected!!.text = "X"
            buselected!!.setBackgroundResource(R.color.blue)
            player1.add(cellId)
            activeplayer = 2
        }
        else
        {
            buselected!!.text = "O"
            buselected!!.setBackgroundResource(R.color.darkgreen)
            player2.add(cellId)
            activeplayer = 1
        }

        buselected.isEnabled = false
        checkwinner()
    }

    fun checkwinner()
    {
        var winner = -1
        //row 1
        if(player1.contains(1) && player1.contains(2) && player1.contains(3))
            winner=1
        if(player2.contains(1) && player2.contains(2) && player2.contains(3))
            winner=2
        //row 2
        if(player1.contains(4) && player1.contains(5) && player1.contains(6))
            winner=1
        if(player2.contains(4) && player2.contains(5) && player2.contains(6))
            winner=2
        //row 3
        if(player1.contains(7) && player1.contains(8) && player1.contains(9))
            winner=1
        if(player2.contains(7) && player2.contains(8) && player2.contains(9))
            winner=2
        //col 1
        if(player1.contains(1) && player1.contains(4) && player1.contains(7))
            winner=1
        if(player2.contains(1) && player2.contains(4) && player2.contains(7))
            winner=2
        //col 2
        if(player1.contains(2) && player1.contains(5) && player1.contains(8))
            winner=1
        if(player2.contains(2) && player2.contains(5) && player2.contains(8))
            winner=2
        //col 3
        if(player1.contains(3) && player1.contains(6) && player1.contains(9))
            winner=1
        if(player2.contains(3) && player2.contains(6) && player2.contains(9))
            winner=2
        //Diagonal
        if(player1.contains(1) && player1.contains(5) && player1.contains(9))
            winner=1
        if(player2.contains(1) && player2.contains(5) && player2.contains(9))
            winner=2
        //Diagonal 2
        if(player1.contains(3) && player1.contains(5) && player1.contains(7))
            winner=1
        if(player2.contains(3) && player2.contains(5) && player2.contains(7))
            winner=2

        if(winner==1)
        {
            Toast.makeText(this, "Player 1 win", Toast.LENGTH_LONG).show()
            restartGame()
        }
        else if(winner==2)
        {
            Toast.makeText(this, "Player 2 win", Toast.LENGTH_LONG).show()
            restartGame()
        }
        if(player1.size + player2.size==9)
        {
            Toast.makeText(this, "Draw", Toast.LENGTH_LONG).show()
            restartGame()
        }
    }

    fun AutoPlay(cellId:Int){

        val buselected:Button? = when(cellId){
            1-> bu1
            2-> bu2
            3-> bu3
            4-> bu4
            5-> bu5
            6-> bu6
            7-> bu7
            8-> bu8
            9-> bu9
            else->{
                bu1
            }
        }
        playgame(cellId,buselected!!)
    }

    fun restartGame()
    {
        activeplayer =1
        player1.clear()
        player2.clear()
        for(cellId : Int in 1..9)
        {
            val buselected : Button ? = when(cellId){
                1 -> bu1
                2 -> bu2
                3 -> bu3
                4 -> bu4
                5 -> bu5
                6 -> bu6
                7 -> bu7
                8 -> bu8
                9 -> bu9
                else ->{bu1}
            }
            buselected!!.text=""
            buselected!!.setBackgroundResource(R.color.whiteblu)
            buselected!!.isEnabled =true
        }
    }

    fun buRequestEvent(view:android.view.View) {
        var friendemail=userdEmail.text.toString()
        myRef.child("Users").child(SplitString(friendemail)).child("Request").push().setValue(myEmail)
        playerOnline(SplitString(myEmail!!) + SplitString(friendemail))

        playerSymbol="X"

    }

    fun buAcceptEvent(view:android.view.View) {
        val friendemail=userdEmail.text.toString()
        myRef.child("Users").child(SplitString(friendemail)).child("Request").push().setValue(myEmail)
        playerOnline(SplitString(friendemail) + SplitString(myEmail!!))

        playerSymbol="O"
    }

    var sessionID:String?=null
    var playerSymbol:String?=null

    fun playerOnline(sessionID:String){
         this.sessionID=sessionID
        myRef.child("PlayerOnline").child(sessionID)
            .addValueEventListener(object : ValueEventListener{

                override fun onDataChange(dataSnapshot:DataSnapshot) {
                    try {
                        val td=dataSnapshot.value as HashMap<*, *>
                        var value:String
                        for (key in td.keys)
                        {
                            value=td[key] as String
                            activeplayer = if (value!=myEmail){
                                if (playerSymbol==="X") 1 else 2
                            }else {
                                if (playerSymbol==="X") 2 else 1
                            }

                            AutoPlay(key.toString().toInt())
                        }
                    }catch (ex:Exception){}
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
    }

    fun incomingCalls(){
        myRef.child("Users").child(SplitString(myEmail!!)).child("Request")
            .addValueEventListener(object : ValueEventListener{

                override fun onDataChange(dataSnapshot:DataSnapshot) {
                    try {
                        val td=dataSnapshot.value as HashMap<*, *>
                        val value:String
                        for (key in td.keys)
                        {
                            value=td[key] as String
                            userdEmail.setText(value)

                            myRef.child("Users").child(SplitString(myEmail!!)).child("Request").setValue(true)
                            break
                        }
                    }catch (ex:Exception){}
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
    }

    fun SplitString(str:String):String{
        val split=str.split("@")
        return split[0]
    }
}