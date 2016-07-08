package com.jzsec.strategy;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.UpdateListener;

import java.util.Map;

/**
 * Created by caodaoxi on 16-7-7.
 */
public class SaleListener implements UpdateListener {

    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            EventType eventType = newEvents[0].getEventType();
            String outputStreamName = eventType.getName();
        }
    }


    public String replaceStrVar(Map<String, Object> map, String template){
        for (Object s : map.keySet()) {
            template = template.replaceAll("\\$\\{".concat(s.toString()).concat("\\}")
                    , map.get(s.toString()).toString());
        }
        return template;
    }
}
