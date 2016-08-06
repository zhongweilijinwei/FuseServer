package com.u8.server.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class CharacterEncodingFilter implements Filter {

//	@Override
//	public void destroy() {
//	}
//
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response,
//			FilterChain chain) throws IOException, ServletException {
////		request.setCharacterEncoding("utf-8");
////		chain.doFilter(request, response);
//	}
//
//	@Override
//	public void init(FilterConfig arg0) throws ServletException {
//	}
	
    private String charset = "UTF-8";  
    private FilterConfig config;  
      
    public void destroy() {  
//        System.out.println(config.getFilterName()+"被销毁");  
        charset = null;  
        config = null;  
    }  
  
    public void doFilter(ServletRequest request, ServletResponse response,  
            FilterChain chain) throws IOException, ServletException {  
        //设置请求响应字符编码  
        request.setCharacterEncoding(charset);  
        response.setCharacterEncoding(charset);  
        //新增加的代码          
        HttpServletRequest req = (HttpServletRequest)request;  
          
        if(req.getMethod().equalsIgnoreCase("get"))  
        {  
            req = new GetHttpServletRequestWrapper(req,charset);  
        }  
          
//        System.out.println("----请求被"+config.getFilterName()+"过滤");  
        //传递给目标servlet或jsp的实际上时包装器对象的引用，而不是原始的HttpServletRequest对象  
        chain.doFilter(req, response);  
          
//        System.out.println("----响应被"+config.getFilterName()+"过滤");  
  
    }  
  
    public void init(FilterConfig config) throws ServletException {  
            this.config = config;  
            String charset = config.getServletContext().getInitParameter("charset");    
            if( charset != null && charset.trim().length() != 0)  
            {  
                this.charset = charset;  
            }  
    }  
    

}
