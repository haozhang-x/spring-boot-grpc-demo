package response;


/**
 * UnKnowResponse.
 */
public class ErrorResponse extends Response {

    /**
     * build an error response.
     *
     * @param errorCode errorCode
     * @param msg       msg
     * @return response
     */
    public static Response build(int errorCode, String msg) {
        ErrorResponse response = new ErrorResponse();
        response.setErrorInfo(errorCode, msg);
        return response;
    }

    /**
     * build an error response.
     *
     * @param msg msg
     * @return response
     */
    public static Response build(String msg) {
        ErrorResponse response = new ErrorResponse();
        response.setErrorInfo(ResponseCode.FAIL.getCode(), msg);
        return response;
    }

    /**
     * build an error response.
     *
     * @param exception exception
     * @return response
     */
    public static Response build(Throwable exception) {
        int errorCode = ResponseCode.FAIL.getCode();
        ErrorResponse response = new ErrorResponse();
        response.setErrorInfo(errorCode, exception.getMessage());
        return response;
    }

}
