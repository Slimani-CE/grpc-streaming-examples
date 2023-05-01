package ma.enset.distributedsystems.service;

import io.grpc.stub.StreamObserver;
import ma.enset.distributedsystems.stubs.Bank;
import ma.enset.distributedsystems.stubs.BankServiceGrpc;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BankGrpcService extends BankServiceGrpc.BankServiceImplBase{
    @Override
    public void convert(Bank.ConvertCurrencyRequest request, StreamObserver<Bank.ConvertCurrencyResponse> responseObserver) {
        String currencyFrom = request.getCurrencyFrom();
        String currencyTo = request.getCurrencyTo();
        double amount = request.getAmount();
        Bank.ConvertCurrencyResponse response = Bank.ConvertCurrencyResponse.newBuilder()
                .setCurrencyFrom(currencyFrom)
                .setCurrencyTo(currencyTo)
                .setAmount(amount)
                .setResult(amount*11)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getCurrencyStream(Bank.ConvertCurrencyRequest request, StreamObserver<Bank.ConvertCurrencyResponse> responseObserver) {
        String currencyFrom = request.getCurrencyFrom();
        String currencyTo = request.getCurrencyTo();
        double amount = request.getAmount();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int counter = 0;
            @Override
            public void run() {
                Bank.ConvertCurrencyResponse response = Bank.ConvertCurrencyResponse.newBuilder()
                        .setCurrencyFrom(currencyFrom)
                        .setCurrencyTo(currencyTo)
                        .setAmount(amount)
                        .setResult(amount*Math.random()*100)
                        .build();
                responseObserver.onNext(response);
                ++counter;
                if (counter == 20) {
                    responseObserver.onCompleted();
                    timer.cancel();
                }
            }
        }, 1000, 1000);
    }

    @Override
    public StreamObserver<Bank.ConvertCurrencyRequest> performCurrencyStream(StreamObserver<Bank.ConvertCurrencyResponse> responseObserver) {
        return new StreamObserver<Bank.ConvertCurrencyRequest>() {
            private double totalAmount;
            private ArrayList<String> listTo = new ArrayList<>();
            @Override
            public void onNext(Bank.ConvertCurrencyRequest convertCurrencyRequest) {
                String from = convertCurrencyRequest.getCurrencyFrom();
                String to = convertCurrencyRequest.getCurrencyTo();
                double amount = convertCurrencyRequest.getAmount();
                totalAmount += amount;
                listTo.add(to);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                String to = listTo.stream().toString();
                Bank.ConvertCurrencyResponse response = Bank.ConvertCurrencyResponse.newBuilder()
                        .setCurrencyFrom("Server")
                        .setCurrencyTo(to)
                        .setAmount(totalAmount)
                        .setResult(123)
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<Bank.ConvertCurrencyRequest> fullCurrencyStream(StreamObserver<Bank.ConvertCurrencyResponse> responseObserver) {
        return new StreamObserver<Bank.ConvertCurrencyRequest>() {
            @Override
            public void onNext(Bank.ConvertCurrencyRequest convertCurrencyRequest) {

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    private int counter = 0;

                    @Override
                    public void run() {
                        System.out.println("Streaming to client...");
                        Bank.ConvertCurrencyResponse response = Bank.ConvertCurrencyResponse.newBuilder()
                                .setCurrencyFrom("blabla")
                                .setCurrencyTo("blabla")
                                .setAmount(counter++)
                                .build();
                        responseObserver.onNext(response);
                        if(counter > 20){
                            responseObserver.onCompleted();
                        }
                    }

                }, 1000, 1000);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        };
    }
}
