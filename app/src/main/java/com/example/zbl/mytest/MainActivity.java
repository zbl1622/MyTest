package com.example.zbl.mytest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean isRunning = true;
    private LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<>();
    private ArrayList<PrintTextTask> printTextTasks = new ArrayList<>();

    private Button btn_addone;
    private TextView tv_content;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tv_content.append(msg.obj + "");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        btn_addone = (Button) findViewById(R.id.btn_addone);
        btn_addone.setOnClickListener(this);
        tv_content = (TextView) findViewById(R.id.tv_content);
//        for (int i = 0; i < 3; i++) {
//            PrintTextTask printTextTask = new PrintTextTask("_" + i);
//            printTextTasks.add(printTextTask);
//            printTextTask.start();
//        }
//        new ScheduleTask().start();
        testRxJava();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        isRunning = false;
        for (PrintTextTask printTextTask : printTextTasks) {
            if (printTextTask.isAlive()) {
                printTextTask.interrupt();
            }
        }
        printTextTasks.clear();
        super.onDestroy();
    }

    private boolean flag = true;

    @Override
    public void onClick(View view) {
        if (view == btn_addone) {
//            try {
//                linkedBlockingQueue.add(".");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            EventBus.getDefault().post(flag ? new MyEvent() : new MyAnotherEvent());
            flag = !flag;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void hehe(SuperEvent event) {
        String text = ".";
        if (event instanceof MyEvent) {
            text = "my";
        } else if (event instanceof MyAnotherEvent) {
            text = "another";
        }
        try {
            linkedBlockingQueue.add(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class PrintTextTask extends Thread {

        private String id = "_0";

        public PrintTextTask(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            while (isRunning) {
                try {
                    String text = linkedBlockingQueue.take();
                    Message msg = handler.obtainMessage();
                    msg.obj = id + ":" + text;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ScheduleTask extends Thread {

        @Override
        public void run() {
            while (isRunning) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                EventBus.getDefault().post(new MyEvent());
            }
        }
    }

    private void testRxJava() {
        String[] items = {"111", "222", "333", "444", "555", "666"};
        Observable<String> observableString = Observable.fromArray(items);
        observableString
//                .map(new Function<String, String>() {
//                    @Override
//                    public String apply(String s) throws Exception {
//                        return s.substring(0, 2);
//                    }
//                })
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String s) throws Exception {
                        return Observable.fromArray(new String[]{s.substring(0, 1), s.substring(1) + "hehe"});
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onComplete() {
                        System.out.println("Observable completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("Oh,no! Something wrong happened！");
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String item) {
                        System.out.println("Item is " + item);
                    }
                });
    }

    private void testStream(){
    }

}
