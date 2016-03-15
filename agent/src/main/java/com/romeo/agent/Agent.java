package com.romeo.agent;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.ui.AIDialog;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by romeo on 15/03/16.
 */
public class Agent {


    public static Agent instance = null;

    private HashMap<String, Intent> intents = new HashMap<>();

    private static AIDataService aiDataService;

    private String speechContext = null;
    private Object subscriber;
    private SpeechResponse setSpeechListener;
    private String TAG = "Agent";

    private Agent() {

    }

    public static void init(String ACCESS_TOKEN, String SUBSCRIPTION_KEY, Context context) {

        final AIConfiguration config = new AIConfiguration(ACCESS_TOKEN, SUBSCRIPTION_KEY,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiDataService = new AIDataService(context.getApplicationContext(), config);

        if (instance == null) {
            instance = new Agent();
        }

    }


    public Agent add(Intent intent) {

        if (intents.containsKey(intent.getAction())) {
            return instance;
        }
        intents.put(intent.getAction(), intent);
        return instance;
    }


    public void execute(String query) {

        final AIRequest request = new AIRequest(query);

        Observable<AIResponse> aiResponseObservable = Observable.create(new Observable.OnSubscribe<AIResponse>() {
            @Override
            public void call(Subscriber<? super AIResponse> subscriber) {

                try {
                    subscriber.onNext(aiDataService.request(request));
                    subscriber.onCompleted();

                } catch (AIServiceException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());


        aiResponseObservable
                .filter(new Func1<AIResponse, Boolean>() {
                    @Override
                    public Boolean call(AIResponse aiResponse) {
                        return ((aiResponse.getResult() != null)
                                && (aiResponse.getResult().getAction() != null)
                                && (!aiResponse.getResult().getAction().equals("")));
                    }
                }).map(new Func1<AIResponse, Object>() {
            @Override
            public Object call(AIResponse aiResponse) {

                Intent intent = intents.get(aiResponse.getResult().getAction());
                return intent.call();
            }
        }).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                Log.v(TAG,"onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Object response) {
                invokeMethod(response);
            }
        });


    }


    private void invokeMethod(Object response) {

        if (subscriber == null) {
            Log.v(TAG, "subscriber null");
            return;
        }

        try {
            Method onAgentResponse = subscriber.getClass().getMethod("onAgentResponse");
            onAgentResponse.invoke(subscriber, response);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }


    public void subscribe(Object newSubscriber) {
        subscriber = newSubscriber;
    }

    public void unSubscribe() {
        subscriber = null;

    }


    public void setSpeechListener(SpeechResponse newSpeechListener) {

        setSpeechListener = newSpeechListener;

    }


}
