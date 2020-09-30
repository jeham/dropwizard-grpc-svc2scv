package eu.hammarback;

import eu.hammarback.service.GetDataReply;
import eu.hammarback.service.GetDataRequest;
import eu.hammarback.service.Item;
import eu.hammarback.service.MyServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class MyServiceImpl extends MyServiceGrpc.MyServiceImplBase {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public void getData(GetDataRequest request, StreamObserver<GetDataReply> responseObserver) {

    logger.info("Got call for identifier: " + request.getIdentifier());

    GetDataReply.Builder builder = GetDataReply.newBuilder()
        .setIdentifier(request.getIdentifier())
        .setTimestamp(System.currentTimeMillis());

    for (int i = 0; i < 5; i++) {
      builder.addItems(Item.newBuilder().setIdentifier(UUID.randomUUID().toString()).build());
    }

    GetDataReply reply = builder.build();
    responseObserver.onNext(reply);
    responseObserver.onCompleted();
  }

}
