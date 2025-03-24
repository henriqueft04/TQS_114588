package tqs;

import java.io.IOException;

public interface ISimpleHttpClient {
    String doHttpGet(String url) throws IOException;
}
