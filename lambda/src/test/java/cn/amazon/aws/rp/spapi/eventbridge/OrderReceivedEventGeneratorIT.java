package cn.amazon.aws.rp.spapi.eventbridge;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

@Ignore
public class OrderReceivedEventGeneratorIT {

    @Test
    public void testPut() {
       final OrderReceivedEventGenerator orderReceivedEventGenerator = new OrderReceivedEventGenerator();

        orderReceivedEventGenerator.put();
    }
}

