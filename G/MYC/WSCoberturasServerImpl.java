package MYC;

import javax.jws.WebService;

@WebService(endpointInterface = "MYC.WSCoberturasServer")
public class WSCoberturasServerImpl implements WSCoberturasServer {

	public String getCoberturas(String host, String name) {
		return "Cobertura " + name + " efetuada!\n";
	}

}
