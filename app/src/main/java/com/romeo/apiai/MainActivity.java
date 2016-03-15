package com.romeo.apiai;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.romeo.agent.Agent;
import com.romeo.agent.Intent;

public class MainActivity extends AppCompatActivity {


    private Button btnSubmit;
    private EditText txtInput;
    private String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Agent.instance.add(new IntentAccountBalance())
                .add(new IntentMiniStatement());


        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        txtInput = (EditText) findViewById(R.id.txtInput);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String input = txtInput.getText().toString().trim();

                if (!input.isEmpty()){
                    Agent.instance.execute(input);
                }


            }
        });

    }


    public void onAgentResponse(Integer integer){

        Log.v(TAG, "onAgentResponse Integer invoked: " + integer);

    }

    public void onAgentResponse(String txt){

        Log.v(TAG,"onAgentResponse String invoked: "+txt);

    }



    @Override
    protected void onResume() {
        super.onResume();
        Agent.instance.subscribe(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Agent.instance.unSubscribe();
    }

    private class IntentMiniStatement extends Intent<Integer>{

        // This is action we will provide
        @Override
        public String getAction() {
            return "mini_statement";
        }

        @Override
        public Integer call() {
            return 2+2;
        }
    }

    public class IntentAccountBalance extends Intent<String>{


        @Override
        public String getAction() {
            return "account_balance";
        }

        @Override
        public String call() {
            return "Testing account balance";
        }
    }

}
