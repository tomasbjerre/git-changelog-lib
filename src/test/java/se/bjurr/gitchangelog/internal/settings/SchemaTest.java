package se.bjurr.gitchangelog.internal.settings;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.net.MalformedURLException;

@RunWith(Parameterized.class)
public class SchemaTest {
    
    private File file;

    public SchemaTest(File jsonSchemaFile) {
        this.file = jsonSchemaFile;
    }
    
    @Test
    public void testSchema() throws MalformedURLException {
        final Settings settings = Settings.fromFile(file.toURI().toURL());
    }
    
    @Parameters
    public static File[] data() {
        return new File("src/test/resources/settings").listFiles();
    }
}
