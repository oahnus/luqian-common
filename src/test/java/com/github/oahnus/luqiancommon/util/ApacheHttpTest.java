package com.github.oahnus.luqiancommon.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

/**
 * Created by oahnus on 2020-09-22
 */
@RunWith(JUnit4.class)
public class ApacheHttpTest {
    @Test
    public void testDownload() {
        String url = "https://cdn.spacetelescope.org/archives/images/publicationjpg/heic1909a.jpg";
        try {
            ApacheHttp.download(url, "C:/D/download2/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
