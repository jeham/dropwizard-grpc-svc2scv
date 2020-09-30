package eu.hammarback;

import io.dropwizard.Application;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerApplication extends Application<ServerConfiguration> {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public void initialize(Bootstrap<ServerConfiguration> bootstrap) {
  }

  @Override
  public void run(ServerConfiguration configuration, Environment environment) {
    environment.jersey().disable();

    Server server = ServerBuilder.forPort(configuration.grpcServerPort)
        .addService(new MyServiceImpl())
        .build();

    environment.lifecycle().manage(new Managed() {
      @Override
      public void start() throws Exception {
        server.start();
        logger.info("grpc server started");
      }

      @Override
      public void stop() {
        logger.info("Shutting down grpc server...");
        server.shutdown();
      }
    });
  }

  public static void main(String[] args) throws Exception {
    new ServerApplication().run(args);
  }

}
