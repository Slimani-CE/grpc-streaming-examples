package ma.enset.distributedsystems.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import ma.enset.distributedsystems.service.BankGrpcService;

import java.io.IOException;

public class GrpcServer {
    public static void main(String[] args) throws IOException {
        Server server = ServerBuilder.forPort(8082)
                .addService(new BankGrpcService())
                .build();
        server.start();
        System.out.println("Server started on port " + server.getPort());
        try {
            server.awaitTermination();
        } catch (InterruptedException e) {
            System.out.println("Server stopped");
            e.printStackTrace();
        }
    }
}
