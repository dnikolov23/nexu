package lu.nowina.nexu;

import org.junit.Assert;
import org.junit.Test;

import lu.nowina.nexu.api.EnvironmentInfo;

public class InternalAPITest {

    @Test
    public void test1() throws Exception {
        
        InternalAPI api = new InternalAPI(null, null, null);
        
        EnvironmentInfo info = api.getEnvironmentInfo();
        Assert.assertNotNull(info.getOs());
        Assert.assertNotNull(info.getArch());
        
    }
    
}
