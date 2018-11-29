import com.google.common.collect.Lists;
import com.wzyx.util.FTPUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class FTPUtil2Test {

    String localFile = "/Users/fengjl/Desktop/1212.png";



    @Test
    public void testUploadFile() throws IOException {
        File file = new File(localFile);
        FTPUtil.uploadFile(Lists.newArrayList(file));

    }

}
