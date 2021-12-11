import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PostHandler  implements Runnable{
  private Connection connection;
  private int basicQos = 1;
//  ExecutorService threadsPool = Executors.newFixedThreadPool(64);

  public PostHandler(Connection connection){
    this.connection = connection;
  }

  @Override
  public void run() {
    try {
      Channel channel = this.connection.createChannel();
      channel.basicQos(this.basicQos);
      channel.queueDeclare("post", false, false, false, null);
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        System.out.println(message);
//        processMessage(message);
      };
      //
      channel.basicConsume("post", false, deliverCallback, consumerTag -> {});
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private void processMessage(String message){
    SkierRecord skierRequest = new Gson().fromJson(message, SkierRecord.class);
    if(!Repository.map.containsKey(skierRequest.getSkierID())){
      Repository.map.put(skierRequest.getSkierID(), new LinkedList<>());
    }
    Repository.map.get(skierRequest.getSkierID()).add(skierRequest);
  }
}
