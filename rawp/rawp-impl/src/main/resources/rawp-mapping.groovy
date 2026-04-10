import dev.lukadjo.rawp.api.RawpMethodType
import dev.lukadjo.rawp.impl.model.RawpRequest

// Default REST mapping:
//   api        <- "api" header
//   methodName <- path (leading slash stripped)
//   methodType <- HTTP method
//   args       <- JSON body (POST only), parsed via Jackson objectMapper binding

def parts = httpRequest.path.replaceFirst("^/", "").split("/", 2)
def api = parts[0]
def methodName = parts[1]
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