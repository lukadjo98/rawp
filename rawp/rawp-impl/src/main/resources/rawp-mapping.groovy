import dev.lukadjo.rawp.api.RawpMethodType
import dev.lukadjo.rawp.impl.model.RawpRequest

// Default REST mapping:
//   api        <- "api" header
//   methodName <- path (leading slash stripped)
//   methodType <- HTTP method
//   args       <- JSON body (POST only), parsed via Jackson objectMapper binding

def api = headers['api']?.get(0)
def methodName = (path?.length() > 1) ? path.substring(1) : null
def methodType = 'POST'.equalsIgnoreCase(httpMethod) ? RawpMethodType.POST : RawpMethodType.GET
def args = [:]

if (methodType == RawpMethodType.POST && body) {
    args = objectMapper.readValue(body, Map.class)
}

def r = new RawpRequest()
r.api = api
r.methodName = methodName
r.methodType = methodType
r.args = args
r