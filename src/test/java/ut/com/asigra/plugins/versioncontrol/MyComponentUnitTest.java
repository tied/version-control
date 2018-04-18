package ut.com.asigra.plugins.versioncontrol;

import org.junit.Test;
import com.asigra.plugins.versioncontrol.api.MyPluginComponent;
import com.asigra.plugins.versioncontrol.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}