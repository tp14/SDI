import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Send {

  private final static String QUEUE_NAME = "queue";

  public static void main(String[] args) throws Exception {
    String host = (args.length < 1) ? null : args[0];
    Integer nome = (args.length < 1) ? 0 : Integer.parseInt(args[1]);
    String message;
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUsername("sdi");
    factory.setPassword("sdi");
    factory.setVirtualHost("vh_sdi");
    factory.setHost("ens5");
    factory.setPort(5672);
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    Scanner sc = new Scanner(System.in);
    String sCurrentLine = sc.nextLine().trim();
    sc.close();

    channel.queueDeclare(QUEUE_NAME, false, false, false, null);

    message = sCurrentLine;

    final String corrId = UUID.randomUUID().toString();
    String replyQueueName = channel.queueDeclare().getQueue();
    AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
        .correlationId(corrId)
        .replyTo(replyQueueName)
        .build();

    channel.basicPublish("", QUEUE_NAME, props, message.getBytes("UTF-8"));

    final CompletableFuture<String> response = new CompletableFuture<>();
    Consumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
          throws IOException {
        if (properties.getCorrelationId().equals(corrId)) {
          response.complete(new String(body, "UTF-8"));
        }
      }
    };
    String ctag = channel.basicConsume(replyQueueName, true, consumer);

    String result = response.get().trim();
    channel.basicCancel(ctag);

    System.out.println("### Cliente(" + nome + ") ####\n" + message + ": " + result);

    channel.close();
    connection.close();
  }
}