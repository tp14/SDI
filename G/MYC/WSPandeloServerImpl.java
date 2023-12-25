package MYC;

import javax.jws.WebService;

@WebService(endpointInterface = "MYC.WSPandeloServer")
public class WSPandeloServerImpl implements WSPandeloServer {

	public String getPandelo(String host, String name) {
		return "Pandelo de " + name + " entregue!";
	}

}
