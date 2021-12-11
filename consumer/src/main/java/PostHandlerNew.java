import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PostHandlerNew {
  private Connection connection;
  private int basicQos = 1;
  ExecutorService threadsPool;
  int numThreads = 64;

  public PostHandlerNew(Connection connection){
    this.connection = connection;
    threadsPool = Executors.newFixedThreadPool(numThreads);
  }


  public void start() {
    for(int i = 0; i < this.numThreads;i++){
      Runnable runner = ()->{
        Channel channel = null;
        try {
          channel = this.connection.createChannel();
          channel.basicQos(this.basicQos);
          channel.queueDeclare("post", false, false, false, null);
        } catch (IOException e) {
          e.printStackTrace();
        }

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
          String message = new String(delivery.getBody(), "UTF-8");
          System.out.println(message);
//        processMessage(message);
        };
        //
        try {
          channel.basicConsume("post", false, deliverCallback, consumerTag -> {});
        } catch (IOException e) {
          e.printStackTrace();
        }
      };
      this.threadsPool.execute(runner);
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
