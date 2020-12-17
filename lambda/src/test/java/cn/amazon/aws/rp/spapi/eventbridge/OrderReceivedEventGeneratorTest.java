package cn.amazon.aws.rp.spapi.eventbridge;

import org.junit.Test;

import static org.junit.Assert.*;

public class OrderReceivedEventGeneratorTest {

    @Test
    public void testPut() {
       final OrderReceivedEventGenerator orderReceivedEventGenerator = new OrderReceivedEventGenerator();

        orderReceivedEventGenerator.put();
    }
}