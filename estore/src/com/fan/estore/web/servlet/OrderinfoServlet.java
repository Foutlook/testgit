package com.fan.estore.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fan.estore.bean.Line;
import com.fan.estore.bean.Order;
import com.fan.estore.service.ILineService;
import com.fan.estore.service.IOrderService;
import com.fan.estore.service.LineServiceImpl;
import com.fan.estore.service.OrderServiceImpl;

@WebServlet("/orderinfoServlet")
public class OrderinfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	ILineService lineService = new LineServiceImpl();
	IOrderService orderService = new OrderServiceImpl();
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String orderid = request.getParameter("orderid");
		try {
			//通过orderid来查询订单项
			List<Line> linesByOId = lineService.findLineByOrderId(Long.parseLong(orderid));
			//查询orderid的order信息
			Order order = orderService.findById(Long.parseLong(orderid));
			session.setAttribute("linesByOId",linesByOId);
			session.setAttribute("order", order);
			//重定向
			response.sendRedirect(request.getContextPath()+"/user/orderinfo.jsp");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} 
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
