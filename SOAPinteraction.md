# Introduction #

Beside the REST interfaces, oBIX also provides a simplified protocol binding to SOAP.

The SOAP endpoint can be accessed using the WSDL description which can be found at:

```
http://localhost:8080/soap?wsdl
```

# Using Java #
Using Java (>= JDK 6) you can use JAX-WS to automatically generate the SOAP client stub classes and interact with the gateway.

Use the following call to create a `obix` client lib for your project.

```
wsimport -d obix -s obix -p obix http://localhost:8080/soap?wsdl
```

This generates the clients stubs including the sources and puts it into the package `obix` within the `obix` directory. If you make this call in the source directory of your java project, the client stubs are immediatelly available. The following sample client shows how to retrieve the oBIX about info from the lobby.

**Sample Java client**
```
import obix.*;

public class TestClient {
      public static void main(String[] args) {
		Obix obix = new Obix();
		ReadReq readReq = new ReadReq();
		readReq.setHref("/obix/about");
		ResponseType about = obix.getObixPort().read(readReq);

		System.out.println("oBIX Server about info.");
		System.out.println("href: " + about.getObj().getHref());
		Str oBIXVersion = (Str) about.getObj().getObjGroup().get(0);
		Str serverName = (Str) about.getObj().getObjGroup().get(1);

		Abstime serverTime = (Abstime) about.getObj().getObjGroup().get(2);
		Abstime serverBootTime = (Abstime) about.getObj().getObjGroup().get(3);
		Str vendorName = (Str) about.getObj().getObjGroup().get(4);
		Uri vendorURL = (Uri) about.getObj().getObjGroup().get(5);
		Str productName = (Str) about.getObj().getObjGroup().get(6);
		Str productVersion = (Str) about.getObj().getObjGroup().get(7);
		Uri productURL = (Uri) about.getObj().getObjGroup().get(8);

		System.out.println("oBIXVersion: " + oBIXVersion.getVal());
		System.out.println("serverName: " + serverName.getVal());
		System.out.println("serverTime: " + serverTime.getVal());
		System.out.println("serverBootTime: " + serverBootTime.getVal());
		System.out.println("vendorName: " + vendorName.getVal());
		System.out.println("vendorURL: " + vendorURL.getVal());
		System.out.println("productName: " + productName.getVal());
		System.out.println("productVersion: " + productVersion.getVal());
		System.out.println("productURL: " + productURL.getVal());
	}

}
```

**Output**
```
oBIX Server about info.
href: /obix/about
oBIXVersion: oBIX 1.1 WD 06
serverName: merlin
serverTime: 2013-04-01T12:46:58.718+02:00
serverBootTime: 2013-04-01T12:44:21.266+02:00
vendorName: Automation Systems Group, Vienna University of Technology
vendorURL: http://www.auto.tuwien.ac.at
productName: IoTSyS gateway
productVersion: 0.1
productURL: http://code.google.com/p/iotsys
```

**Query temperature history**

```
import java.math.BigInteger;
import java.util.Iterator;

import obix.*;

public class TestClient {
        public static void main(String[] args){
                
                Obix obix = new Obix();
                OBIXSoapPort obixPort = obix.getObixPort();
                InvokeReq historyQuery = new InvokeReq();
                historyQuery.setHref("/simTemp/value/history/query");

                Int count = new Int();
                count.setVal(BigInteger.valueOf(5));
                count.setName("limit"); 
                Obj filterObj = new Obj();
                filterObj.setIs("obix:HistoryFilter");
                filterObj.getObjGroup().add(count);
                historyQuery.setObj(filterObj);
                
                ResponseType historyResponse = obixPort.invoke(historyQuery);
                
                Int cnt = (Int) historyResponse.getObj().getObjGroup().get(0);
                Abstime start = (Abstime) historyResponse.getObj().getObjGroup().get(1);
                Abstime end = (Abstime) historyResponse.getObj().getObjGroup().get(2);
                
                System.out.println("Count: " + cnt.getVal());
                System.out.println("Start: " + start.getVal());
                System.out.println("End: " + end.getVal());
                
                List list = (List) historyResponse.getObj().getObjGroup().get(3);
                System.out.println("List of: " + list.getOf());
                Iterator<Object> listIt = list.getObjGroup().iterator();
                while(listIt.hasNext()){ 
                        Obj elem = (Obj) listIt.next();
                        Abstime date = (Abstime) elem.getObjGroup().get(0);
                        Real real = (Real) elem.getObjGroup().get(1);
                        System.out.println(date.getVal() + ": " + real.getVal());
                }
        }
}
```

**Output:**
```
Count: 5
Start: 2013-06-19T21:33:13.333+02:00
End: 2013-06-19T21:45:49.517+02:00
List of: obix:HistoryRecord
2013-06-19T21:33:13.333+02:00: 18.0
2013-06-19T21:45:40.512+02:00: 18.200000000000003
2013-06-19T21:45:43.514+02:00: 18.300000000000004
2013-06-19T21:45:46.516+02:00: 18.500000000000007
2013-06-19T21:45:49.517+02:00: 18.60000000000001
```