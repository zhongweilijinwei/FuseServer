package com.u8.server.sdk.chubaodianhua;

/**
 * 公用常量类。
 * 
 * @author gavin.gu
 * @since 1.0, Jun 08, 2015
 */
public abstract class Constants {

	/** Touchlife默认时间格式 **/
	public static final String DATE_TIME_FORMAT = "yyyyMMddHHmmss";

	/** Date默认时区 **/
	public static final String DATE_TIMEZONE = "GMT+8";

	/** API版本号 **/
	public static final String API_VERSION = "1.1";

	/** UTF-8字符集 **/
	public static final String CHARSET_UTF8 = "UTF-8";

	/** MD5签名方式 */
	public static final String SIGN_TYPE_MD5 = "MD5";

	/** RSA签名方式 */
	public static final String SIGN_TYPE_RSA = "RSA";

	/** RSA签名算法 */
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	/** SDK版本号 */
	public static final String SDK_VERSION = "20150608";

	/** 响应参数result*/
	public static final String RESULT_SUCCESS = "SUCCESS";
	public static final String RESULT_FAIL = "FAIL";

	/** 响应编码 */
	public static final String CONTENT_ENCODING_GZIP = "gzip";

    /** 客户端检测错误码(包含request参数错误和response参数错误) */
	public static final String ERROR_CODE_ARGUMENTS_MISSING = "1";
	public static final String ERROR_CODE_ARGUMENTS_INVALID = "2";
	public static final String ERROR_CODE_SIGNATURE_INVALID = "3";

    /** 服务端返回错误码 */
    //2000 ~ 2999 访问成功
    //4000 ~ 4999 客户端错误(如缺少必选参数、accessToken未授权等)
    //5000 ~ 5999 服务器内部错误
}

