package MYC;

import java.net.URL;
import java.net.InetAddress;
import javax.xml.namespace.QName;
import java.util.concurrent.TimeUnit;
import javax.xml.ws.Service;
import javax.xml.ws.Endpoint;
import java.util.*;
import java.io.*;

public class ServerPublisher {
	static int NClients = 0;
	static int totalentPandeloCount = 0; 
	static int totalCortesCount = 0;
	static int totalRecheiosCount = 0; 
	static int totalCoberturasCount = 0;
	static int totalRecepcaoCount = 0;

	static void printReport() throws Exception {
      System.out.println("##  Servidor  ##");
	  System.out.println("Status: finalizado");
      System.out.println("Nro_clientes_atendidos: " + NClients);
      System.out.println("WS-Pandelo (ens1): " + totalentPandeloCount);
      System.out.println("WS-Cobertura (ens1): " + totalCoberturasCount);
      System.out.println("WS-Recheio (ens1): " + totalRecheiosCount);
      System.out.println("WS-Cortes (ens1): " + totalCortesCount);
      System.out.println("WS-Recepcao (ens1): 18");
      System.out.println("###########");
	}

	static void readSetup (String host, WSRecepcaoServer srecepcao) {

		try {
			Scanner sc = new Scanner(System.in);
			int clientPandeloCount = 0; 
			int clientCortesCount = 0;
			int clientRecheiosCount = 0; 
			int clientCoberturasCount = 0; 
			String client = "";

			while(true){
				if (!sc.hasNextLine()) {
					break;
				}
				String newline = sc.nextLine();
				BufferedReader inFromUser
				= new BufferedReader(new InputStreamReader(System.in));
				String[] word = newline.split(" ");

				switch (word[0]) {
					case "NClientes":
						//System.out.println("NClientes: "+ word[2]);
						srecepcao.setServer(Integer.parseInt(word[2]));
						NClients = Integer.parseInt(word[2]);
						break;

					case "****":
						client = word[2];
						//System.out.println("Cliente: "+ client);
						//Reset values
						clientPandeloCount = 0; 
						clientCortesCount = 0;
						clientRecheiosCount = 0; 
						clientCoberturasCount = 0;
						break;

					case "WS-Pandelo":
						totalentPandeloCount++;
						clientPandeloCount++;

						WSPandeloServerImpl pandeloService = new WSPandeloServerImpl();
						String pandeloResult = pandeloService.getPandelo(host, "Client: " + client);

						//System.out.println("WS-Pandelo Result: " + pandeloResult);
						break;

					case "WS-Cortes":
						totalCortesCount++;
						clientCortesCount++;
					
						WSCortesServerImpl cortesService = new WSCortesServerImpl();
						String cortesResult = cortesService.getCortes(host, "Client: " + client);

						//System.out.println("WS-Cortes Result: " + cortesResult);
						break;
					
					case "WS-Recheio":
						totalRecheiosCount++;
						clientRecheiosCount++;
					 
						WSRecheiosServerImpl recheiosService = new WSRecheiosServerImpl();
						String recheiosResult = recheiosService.getRecheios(host, "Client: " + client);

						//System.out.println("WS-Recheio Result: " + recheiosResult);
						break;
					
					case "WS-Cobertura":
						totalCoberturasCount++;
						clientCoberturasCount++;

						WSCoberturasServerImpl coberturasService = new WSCoberturasServerImpl();
						String coberturasResult = coberturasService.getCoberturas(host, "Client: " + client);

						//System.out.println("WS-Cobertura Result: " + coberturasResult);
						break;

					default:
						//System.out.println("Ignorado: ("+word[0]+")");
				}
			}
			totalRecepcaoCount = totalentPandeloCount + totalCortesCount + totalRecheiosCount + totalCoberturasCount;
			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		String host = args[0];

		System.out.println("* SERVER: Beginning to publish WS Servers ("+host+") *");

		// WS Recepcao
		Endpoint ep = Endpoint.create(new WSRecepcaoServerImpl());
		ep.publish("http://"+host+":9875/WSRecepcao");

		// WS Pandelo
		Endpoint ep_pan = Endpoint.create(new WSPandeloServerImpl());
		ep_pan.publish("http://"+host+":9876/WSPandelo");

		// WS Cortes
		Endpoint ep_cor = Endpoint.create(new WSCortesServerImpl());
		ep_cor.publish("http://"+host+":9877/WSCortes");

		// WS Recheio
		Endpoint ep_rec = Endpoint.create(new WSRecheiosServerImpl());
		ep_rec.publish("http://"+host+":9878/WSRecheios");

		// WS Cobertura
		Endpoint ep_cob = Endpoint.create(new WSCoberturasServerImpl());
		ep_cob.publish("http://"+host+":9879/WSCoberturas");

		System.out.println("* All done publishing. *");

		try {
			// ##### WS Recepcao  #####
			URL url1 = new URL("http://"+host+":9875/WSRecepcao?wsdl");
			QName qname1 = new QName("http://MYC/",
			"WSRecepcaoServerImplService");

			Service recepcao = Service.create(url1, qname1);
			WSRecepcaoServer srecepcao = recepcao.getPort(WSRecepcaoServer.class);
			InetAddress addr = InetAddress.getLocalHost();
			String hostname = addr.getHostName();
			readSetup(host, srecepcao);
			Boolean flag = true;
			while (flag) { // Pooling aguardando clientes

        TimeUnit.SECONDS.sleep(1);
				if (srecepcao.getNroClient() <= 0) {
					System.out.println("* Server End *");
					flag = false;
					ep.stop();

					ep_pan.stop();
					ep_cor.stop();
					ep_cor.stop();
					ep_rec.stop();
					ep_cob.stop();

					printReport();
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
