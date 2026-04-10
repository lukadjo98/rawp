import dev.lukadjo.rawp.api.RawpMethodType
import dev.lukadjo.rawp.impl.model.RawpRequest

def api = ( httpRequest.path?.length() > 1 ) ? httpRequest.path.substring(1) : null;
def methodName = httpRequest.headers['soapaction']?.get(0);
def methodType = 'POST'.equalsIgnoreCase(httpRequest.httpMethod) ? RawpMethodType.POST : RawpMethodType.GET
def args = [:]

if (methodType == RawpMethodType.POST && httpRequest.body) {
    def envelope = xmlMapper.readValue(httpRequest.body, Map.class)
    args = envelope?.Body
}

def r = new RawpRequest()
r.api = api
r.methodName = methodName
r.methodType = methodType
r.args = args
r