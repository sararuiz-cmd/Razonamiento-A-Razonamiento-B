package proyecto.com.Razonamiento_A_B.web;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

public final class JsonUtil {

    private JsonUtil() {
    }

    public static void json(HttpServletResponse response, String body) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(body);
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder out = new StringBuilder(value.length() + 16);
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"': out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\b': out.append("\\b"); break;
                case '\f': out.append("\\f"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (c < 32) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
            }
        }
        return out.toString();
    }

    public static String error(String message) {
        return "{\"ok\":false,\"mensaje\":\"" + escape(message) + "\"}";
    }

    public static String ok(String message) {
        return "{\"ok\":true,\"mensaje\":\"" + escape(message) + "\"}";
    }
}
