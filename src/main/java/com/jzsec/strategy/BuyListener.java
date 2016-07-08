package com.jzsec.strategy;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.client.UpdateListener;
import com.stock.quota.utils.DBUtils;

import java.util.Map;

/**
 * Created by caodaoxi on 16-7-7.
 */
public class BuyListener implements UpdateListener{

    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            EventType eventType = newEvents[0].getEventType();
            Map<String, Object> event = (Map<String, Object>) newEvents[0].getUnderlying();
            String outputStreamName = eventType.getName();
            DBUtils.recordTrade(event, 1, 1);
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
