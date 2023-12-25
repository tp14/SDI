package MYC;

import javax.jws.WebService;

@WebService(endpointInterface = "MYC.WSRecheiosServer")
public class WSRecheiosServerImpl implements WSRecheiosServer {

	public String getRecheios(String host, String name) {
		return "Recheio " + name + " efetuado!";
	}

}
