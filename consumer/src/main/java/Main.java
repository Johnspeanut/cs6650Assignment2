import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class Main {
  private final static String QUEUE_NAME = "post";
//  private static String ipAddress = "18.210.38.98";
  private static String ipAddress = "localhost";
  private static String userName = "guest";
  private static String passwd = "guest";
  private static int THREAD_NUMBER = 128;
  private final static ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();

//  public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
//    ConnectionFactory factory = new ConnectionFactory();
//    factory.setHost(ipAddress);
//    factory.setUsername(userName);
//    factory.setPassword(passwd);
//    Connection connection = factory.newConnection();
//    Thread[] tids = new Thread[THREAD_NUMBER];
//    for (int i = 0; i < THREAD_NUMBER; i++) {
//      tids[i] = new Thread(new PostHandler(connection));
//      tids[i].start();
//    }
//
//  }


  public static void main(String[] args) throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(ipAddress);
//    factory.setUsername(userName);
//    factory.setPassword(passwd);
    final Connection connection = factory.newConnection();

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try{
          final Channel channel = connection.createChannel();
          channel.queueDeclare(QUEUE_NAME, false, false,false,null);
          channel.basicQos(10); //max one message per receiver
          System.out.println("[x] Awaiting PRC requests");

          Object monitor = new Object();
          DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            AMQP.BasicProperties replyProps = new BasicProperties.Builder().correlationId(delivery.getProperties().getCorrelationId()).build();
            String response = "";
            try {
              String message = new String(delivery.getBody(), "UTF-8");
              message = message.replaceAll("^\"+|\"+$", "");
              System.out.println("Receive : " + message);
              String[] result = message.split(",");
//              for(int j = 0; j < result.length; j++){
//                System.out.println(j + ":" + result[j]);
//              }
              int liftId = Integer.valueOf(result[5]);
              map.put(liftId, map.getOrDefault(liftId, 0) + 1);
//              System.out.println("Map : " + map.toString());
            }catch (RuntimeException e){
              System.out.println("Error : " + e.toString());
            }finally {
              channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
              synchronized (monitor){
                monitor.notify();
              }
            }


//            String message = new String(delivery.getBody(), "UTF-8");
//            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//            System.out.println( "Callback thread ID = " + Thread.currentThread().getId() + " Received '" + message + "'");
//            String[] result = message.split(",");
//            int liftId = Integer.valueOf(result[0]);
//            int skierId = Integer.valueOf(result[1]);
//            // update hashmap
//            List<Integer> list = map.getOrDefault(skierId, Collections.synchronizedList(new ArrayList<Integer>()));
//            list.add(liftId);
//            map.put(skierId, list);
          };

          // process messages
          channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
          while(true){
            synchronized (monitor){
              try{
                monitor.wait();
              }catch (InterruptedException e){
                e.printStackTrace();
              }
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
    for(int i = 0; i < THREAD_NUMBER; i++){
      Thread thread = new Thread(runnable);
      thread.start();
    }

  }
}
