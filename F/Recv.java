import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.regex.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Locale;



import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Recv {

  private final static String QUEUE_NAME = "queue";

  public static List<Elements> splitString(String input) {
    Pattern pattern = Pattern.compile("([A-Z][a-z]?)|([0-9]+\\.[0-9]+[A-Z][a-z]?)");
    Matcher matcher = pattern.matcher(input);
    List<Elements> substrings = new ArrayList<Elements>();

    while (matcher.find()) {
      String element = matcher.group();
      if (element != null) {
        int qtd = 1;
        int offset = 0;
        String symbol = element;
        Pattern pattern_element = Pattern.compile("([0-9]+)\\.([0-9]+)([A-Z][a-z]?)");
        Matcher matcher_element = pattern_element.matcher(element);
        try {

          if (matcher_element.find()) {
            qtd = Integer.parseInt(matcher_element.group(1));
            offset = Integer.parseInt(matcher_element.group(2));
            symbol = matcher_element.group(3);
          }
          substrings.add(new Elements(symbol, qtd, offset));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    }

    return substrings;
  }

  public static void main(String[] args) throws Exception {
    Integer nClientes = (args.length < 1) ? 0 : Integer.parseInt(args[0]);

    ConnectionFactory factory = new ConnectionFactory();
    factory.setUsername("sdi");
    factory.setPassword("sdi");
    factory.setVirtualHost("vh_sdi");
    factory.setHost("ens5");
    factory.setPort(5672);
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    channel.queuePurge(QUEUE_NAME);

    Consumer consumer = new DefaultConsumer(channel) {
      int responded = 0;

      @Override
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
          throws IOException {
        String message = new String(body, "UTF-8");
        List<Elements> elements = splitString(message);
        String response = new String(
            elements.stream().map(element -> element.getAscii()).map(String::valueOf).collect(Collectors.joining()));
        AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
            .correlationId(properties.getCorrelationId())
            .build();

        channel.basicPublish("", properties.getReplyTo(), replyProps, response.getBytes("UTF-8"));
      }
    };
    channel.basicConsume(QUEUE_NAME, true, consumer);
  }

}

final class Elements {
    private String simbol;
    private int qtd;
    private int offset;

    public Elements(String simbol, int qtd, int offset) {
        this.simbol = simbol;
        this.qtd = qtd;
        this.offset = offset;
    }

    public char getAscii() {
        return (char) (mapSimbolToInt.get(this.simbol) * this.qtd + this.offset + 31);
    }

    public String toString() {
        return String.format(Locale.getDefault(), "(%s %s %s)", this.simbol, this.qtd, this.offset);
    }

    private static final HashMap<String, Integer> mapSimbolToInt = new HashMap<String, Integer>() {
        {
            put("H", 1);
            put("He", 2);
            put("Li", 3);
            put("Be", 4);
            put("B", 5);
            put("C", 6);
            put("N", 7);
            put("O", 8);
            put("F", 9);
            put("Ne", 10);
            put("Na", 11);
            put("Mg", 12);
            put("Al", 13);
            put("Si", 14);
            put("P", 15);
            put("S", 16);
            put("Cl", 17);
            put("Ar", 18);
            put("K", 19);
            put("Ca", 20);
            put("Sc", 21);
            put("Ti", 22);
            put("V", 23);
            put("Cr", 24);
            put("Mn", 25);
            put("Fe", 26);
            put("Co", 27);
            put("Ni", 28);
            put("Cu", 29);
            put("Zn", 30);
            put("Ga", 31);
            put("Ge", 32);
            put("As", 33);
            put("Se", 34);
            put("Br", 35);
            put("Kr", 36);
            put("Rb", 37);
            put("Sr", 38);
            put("Y", 39);
            put("Zr", 40);
            put("Nb", 41);
            put("Mo", 42);
            put("Tc", 43);
            put("Ru", 44);
            put("Rh", 45);
            put("Pd", 46);
            put("Ag", 47);
            put("Cd", 48);
            put("In", 49);
            put("Sn", 50);
            put("Sb", 51);
            put("Te", 52);
            put("I", 53);
            put("Xe", 54);
            put("Cs", 55);
            put("Ba", 56);
            put("La", 57);
            put("Ce", 58);
            put("Pr", 59);
            put("Nd", 60);
            put("Pm", 61);
            put("Sm", 62);
            put("Eu", 63);
            put("Gd", 64);
            put("Tb", 65);
            put("Dy", 66);
            put("Ho", 67);
            put("Er", 68);
            put("Tm", 69);
            put("Yb", 70);
            put("Lu", 71);
            put("Hf", 72);
            put("Ta", 73);
            put("W", 74);
            put("Re", 75);
            put("Os", 76);
            put("Ir", 77);
            put("Pt", 78);
            put("Au", 79);
            put("Hg", 80);
            put("Tl", 81);
            put("Pb", 82);
            put("Bi", 83);
            put("Po", 84);
            put("At", 85);
            put("Rn", 86);
            put("Fr", 87);
            put("Ra", 88);
            put("Ac", 89);
            put("Th", 90);
            put("Pa", 91);
            put("U", 92);
            put("Np", 93);
            put("Pu", 94);
            put("Am", 95);
            put("Cm", 96);
            put("Bk", 97);
            put("Cf", 98);
            put("Es", 99);
            put("Fm", 100);
            put("Md", 101);
            put("No", 102);
            put("Lr", 103);
            put("Rf", 104);
            put("Db", 105);
            put("Sg", 106);
            put("Bh", 107);
            put("Hs", 108);
            put("Mt", 109);
            put("Ds", 110);
            put("Rg", 111);
            put("Cn", 112);
            put("Nh", 113);
            put("Fl", 114);
            put("Mc", 115);
            put("Lv", 116);
            put("Ts", 117);
            put("Og", 118);
        }
    };
}