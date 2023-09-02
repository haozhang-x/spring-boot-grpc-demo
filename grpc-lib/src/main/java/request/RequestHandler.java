package request;


import response.Response;

/**
 * based request handler.
 */
public abstract class RequestHandler<T extends Request, S extends Response> {

    /**
     * Handler request.
     *
     * @param request request
     * @param meta    request meta data
     * @return response
     */
    public Response handleRequest(T request, RequestMeta meta) {
        return handle(request, meta);
    }

    /**
     * Handler request.
     *
     * @param request request
     * @param meta    request meta data
     * @return response
     */
    public abstract S handle(T request, RequestMeta meta);

}
