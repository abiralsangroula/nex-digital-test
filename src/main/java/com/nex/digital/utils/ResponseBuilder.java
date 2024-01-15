package com.nex.digital.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseBuilder {
	public static enum ResponseType {
		SUCCESS, ERROR
	}

	public static Map<String, Object> buildObjectResponse(Object data, ResponseCode responseCode,
			ResponseType responseType, String description) {
		Map<String, Object> resp = new HashMap<String, Object>();
		resp.put("status", responseType.toString());
		resp.put("statusCode", responseCode.getCode());
		resp.put("description", description);
		resp.put("data", data);
		return resp;
	}

	public static Map<String, Object> buildMapResponse(Map data, ResponseCode responseCode, ResponseType responseType,
			String description) {
		Map<String, Object> resp = new HashMap<String, Object>();
		resp.put("status", responseType.toString());
		resp.put("statusCode", responseCode.getCode());
		resp.put("description", description);
		resp.put("data", data);
		return resp;
	}

	public static Map<String, Object> buildListResponse(Object object, ResponseCode responseCode, ResponseType responseType,
			String description) {
		Map<String, Object> resp = new HashMap<String, Object>();
		resp.put("status", responseType.toString());
		resp.put("statusCode", responseCode.getCode());
		resp.put("description", description);
		resp.put("data", object);
		return resp;
	}

	public static ResponseEntity<Map<String, Object>> buildErrorEntityResponseException(ResponseCode code,
			Exception e) {
		ResponseType responseType = ResponseType.ERROR;
		if (code == null)
			code = ResponseCode.SERVER_ERROR_STATUS_CODE;

		return new ResponseEntity<Map<String, Object>>(ResponseBuilder.buildObjectResponse(null, code, responseType,
				e == null ? code.getMsg() : e.getMessage()), code.getHttpCode());
	}

	public static ResponseEntity<Map<String, Object>> buildErrorEntityResponse(ResponseCode code, String msg) {
		ResponseType responseType = ResponseType.ERROR;
		if (code == null)
			code = ResponseCode.SERVER_ERROR_STATUS_CODE;

		return new ResponseEntity<Map<String, Object>>(
				ResponseBuilder.buildObjectResponse(null, code, responseType, msg), code.getHttpCode());
	}

	public static ResponseEntity<Map<String, Object>> buildSuccessEntityResponse(Object data, String responseMsg) {
		ResponseType responseType = ResponseType.SUCCESS;
		ResponseCode code = ResponseCode.OK_STATUS_CODE;

		return new ResponseEntity<Map<String, Object>>(ResponseBuilder.buildObjectResponse(data, code, responseType,
				(responseMsg == null || responseMsg.equals("") ? ResponseType.SUCCESS.toString() : responseMsg)),
				code.getHttpCode());
	}

	public static enum ResponseCode {
		OK_STATUS_CODE(200, "OK", HttpStatus.OK),
		SERVER_ERROR_STATUS_CODE(500, "Internal Server Error!", HttpStatus.INTERNAL_SERVER_ERROR),
		INVALID_INPUT_STATUS_CODE(4001, "Invalid Input Parameters!", HttpStatus.BAD_REQUEST),
		RESOURCE_NOT_FOUND(404, "Requested Resource Not Found!", HttpStatus.NOT_FOUND);

		private int code;
		private String msg;
		private HttpStatus httpCode;

		ResponseCode(int code, String msg, HttpStatus httpCode) {
			this.code = code;
			this.msg = msg;
			this.httpCode = httpCode;
		}

		public int getCode() {
			return code;
		}

		public String getMsg() {
			return msg;
		}

		public HttpStatus getHttpCode() {
			return httpCode;
		}
	}
}

