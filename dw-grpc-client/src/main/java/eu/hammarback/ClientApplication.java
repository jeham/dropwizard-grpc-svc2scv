package eu.hammarback;

import eu.hammarback.service.GetDataReply;
import eu.hammarback.service.GetDataRequest;
import eu.hammarback.service.MyServiceGrpc;
import io.dropwizard.Application;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ClientApplication extends Application<ClientConfiguration> {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public void initialize(Bootstrap<ClientConfiguration> bootstrap) {
  }

  @Override
  public void run(ClientConfiguration configuration, Environment environment) {
    environment.jersey().disable();

    ManagedChannel channel = ManagedChannelBuilder.forTarget(configuration.grpcTarget)
        .usePlaintext()
        .build();

    MyServiceGrpc.MyServiceBlockingStub blockingStub = MyServiceGrpc.newBlockingStub(channel);

    environment.lifecycle().manage(new Managed() {
      @Override
      public void start() {
      }

      @Override
      public void stop() {
        channel.shutdownNow();
      }
    });

    environment.lifecycle().scheduledExecutorService("fetch-thread").threads(1).build().scheduleWithFixedDelay(() -> {

      logger.info("Fetching data...");
      try {
        GetDataRequest request = GetDataRequest.newBuilder().setIdentifier(UUID.randomUUID().toString()).build();
        GetDataReply reply = blockingStub.getData(request);
        logger.info("Reply: " + reply);
      } catch (Exception e) {
        logger.warn(e.getMessage());
      }

    }, 1, 5, TimeUnit.SECONDS);

  }

  public static void main(String[] args) throws Exception {
    new ClientApplication().run(args);
  }

}
