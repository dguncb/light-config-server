package net.lightapi.config.server.common;

import net.lightapi.config.server.common.template.TemplatesFileLoader;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import java.util.List;

public class TemplatesFileLoaderTest {

    private static TemplatesFileLoader fileLoader;
    @BeforeClass
    public static void setUp() {
        fileLoader = new TemplatesFileLoader();

    }

/*
    @Test
    public void testGetTemplates()  throws IOException {
        List<File> files =  fileLoader.getTemplates("/Users/chenga/workspace/light-config-server/src/test/resources/config");
        System.out.println("result:" + files.get(0).getName());

    }
    */
}
