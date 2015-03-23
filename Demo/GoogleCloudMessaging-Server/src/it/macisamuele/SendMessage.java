package it.macisamuele;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.android.gcm.server.GCMSender;
import com.google.android.gcm.server.Message.Builder;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

/**
 * Servlet implementation class SendMessage
 */
@WebServlet("/SendMessage")
public class SendMessage extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("deprecation")
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        String senderKey = request.getParameter("senderKey");
        String deviceID = request.getParameter("deviceID");
        if (senderKey != null && deviceID != null && senderKey.length()>0 && deviceID.length()>0) {
            Sender sender = GCMSender.getInstance(senderKey);
            Builder builder = new Builder();
            builder.addData("text", new Date().toGMTString());
            Result result = sender.send(builder.build(), deviceID, 1);
            response.getWriter().append(result.toString());
        }
        else {
            response.getWriter().append("senderKey and deviceID attributes should be defined and valid");
        }
    }

}
