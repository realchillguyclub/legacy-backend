package server.poptato.global.wrapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CustomHttpRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] requestBody;

    public CustomHttpRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        try (InputStream is = request.getInputStream(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            this.requestBody = baos.toByteArray();
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream bais = new ByteArrayInputStream(this.requestBody);
        return new ServletInputStream() {
            @Override public int read() {
                return bais.read();
            }
            @Override public boolean isFinished() {
                return bais.available() == 0;
            }
            @Override public boolean isReady() {
                return true;
            }
            @Override public void setReadListener(ReadListener readListener) {
            }
        };
    }

    public byte[] getRequestBody() {
        return this.requestBody;
    }
}
