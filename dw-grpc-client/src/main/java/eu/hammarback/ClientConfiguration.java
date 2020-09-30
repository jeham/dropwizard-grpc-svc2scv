package eu.hammarback;

import io.dropwizard.Configuration;

public class ClientConfiguration extends Configuration {

  public String grpcTarget = "localhost:8765";

}
