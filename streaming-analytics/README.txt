Servlet
  ^  ^
  |  |____>ActionServlet(HttpRequest, HttpResponse)
  |
  |
  |
HttpServlet(HttpRequest, HttpResponse)



ProtocolServlet
|_HttpServlet
|_MQServlet
|_SocketServlet (socket)
|_

HttpProtocolServlet extends HttpServlet extends GenricServlet
 - HttpContent (byte[]) -> object rep. {string,json,object,map,list}
 convertValue(byte[], expectedType) -> ExpectedType
 
 - If Payload JSON JSONConverter -> String, JSONObject, Object, map, list
 - If Payload Object ObjectConverter
MQProtocolServlet extends GenericServlet

SocketProtocolServlet extends GenericServlet
  -SocketContent(byte[]) -> object rep {string,json,object,map,list,txn}

------------------------------------------------------------------------------
NettyHandler																	|
		|																		|
		| (network byte auto-detection determines correct protocolrequest/response)		|
	| (calls correct protocol servlet)? 																		|
																				|
ProtocolServlet:HttpServlet,MQServlet,ObjectServlet								|
---------------
		| in:HttpRequest out:HttpResponse									|
		| (converts protocol body to Java Object)								|
		| service(httpReq, httpResp)
		|
ActionServlet:eg: ExprEvalServlet
===============																	|
		| in:ActionRequest out:ActionResponse
		| (sums two numbers)													|
		| execute(in:object, out:object)
		| service(actReq, actResp)
--------------------------------------------------------------------------------|

{HttpServlet->ExprEvalServlet->FooServlet} - forward(ExprServlet)
{MQServlet->ExprEvalServlet}

filter (detects) -> forward... 


HttpEndPoint

Payload/Protocol
JSON/HTTP -> ProtocolRequest has a Payload{ JSON, Object, ByteBuf, Transaction } -> Object
Object/HTTP
Object/JMS
JSON/JMS
