package net.helipilot50.aerospike.cdt.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException.Connection;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.Value;
import com.aerospike.client.cdt.CTX;
import com.aerospike.client.cdt.MapOperation;
import com.aerospike.client.cdt.MapOrder;
import com.aerospike.client.cdt.MapPolicy;
import com.aerospike.client.cdt.MapWriteFlags;

enum EventType{
	CLICK("click"),
	IMPRESSION("impression"),
	VISIT("visit"),
	CONVERSION("conversion");
	
	public final String label;
	
	private static EventType[] list = EventType.values();

    public static EventType getEvent(int i) {
        return list[i];
    }
	 
    private EventType(String label) {
        this.label = label;
    }
}

public class CDTMapExample extends TimerTask{
	
    private static final List<String> campaignKeys = new ArrayList<String>() {
        private static final long serialVersionUID = 408064932585810852L;

        {
            add("achme-123");
            add("teslayed-456");
            add("maesked-789");
            add("blue-destination-189");
            add("masda-289");
            add("amazingon-281");
            add("quaintus-928");
            add("hzbc-928");
            add("marryit-921");
            add("the-desk-trade-931");
            add("form-ad-2019");
            add("4-mobile-2019");
        }
    };
    


    private static String randomCampaign() {
        int index = (int) (Math.random() * campaignKeys.size());
        return campaignKeys.get(index);
    }

    private static EventType randomEvent() {
        int index = (int) (Math.random() * 4);
        return EventType.getEvent(index);
    }

	private AerospikeClient asClient = null;

	public CDTMapExample() {
        int attempts = 0;
        boolean attemptConnection = true;
        while (attemptConnection) {
            attempts += 1;
            try {
                System.out.println("Connect to aerospike, attempt: " + attempts);
                this.asClient = new AerospikeClient(Constants.AEROSPILE_HOST, 3000);
                attemptConnection = false;
            } catch (Connection conn) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (attempts > 2) {
                    attemptConnection = false;
                    System.out.println("Cannot connect to aerospike, attempted: " + attempts);
                    throw conn;
                }
            }
        }
        for (String campaign : campaignKeys) {
        	setupCampaignData(campaign);
        }
	}
	
	/**
	 * Creates a campaign record and
	 * initializes campaign counters to zero
	 * @param campaign
	 */
    private void setupCampaignData(String campaign) {
    	Key key = new Key(Constants.NAMESPACE, Constants.RECORD_SET, campaign);
    	String binName = Constants.DATA_BIN;
    	Map<Value,Value> campaignaDetails = new HashMap<Value,Value>();
    	Map<Value,Value> dataCube = new HashMap<Value,Value>();
    	
//    	campaignDetails: {
//    		dataCube: {
//	    		clicks: 0,
//	    		impressions: 0,
//	    		visits: 0,
//	    		conversions: 0
//	    	}
//    	}

    	campaignaDetails.put(Value.get("dataCube"), Value.get(dataCube));
    	
		dataCube.put(Value.get("clicks"), Value.get(0));
		dataCube.put(Value.get("impressions"), Value.get(0));
		dataCube.put(Value.get("visits"), Value.get(0));
		dataCube.put(Value.get("conversions"), Value.get(0));
		
		
		MapPolicy mapPolicy = new MapPolicy(MapOrder.KEY_ORDERED, (MapWriteFlags.DEFAULT | MapWriteFlags.NO_FAIL));
		Record record = asClient.operate(null, key,
			MapOperation.putItems(mapPolicy, binName, campaignaDetails)
		);
    }

    public void addEvent(String campaign, EventType event) {
		Key key = new Key(Constants.NAMESPACE, Constants.RECORD_SET, campaign);
		String binName = Constants.DATA_BIN;
		Value element = Value.get(event.label);
		Record record = asClient.operate(null, key,
		MapOperation.increment(MapPolicy.Default, binName, element, Value.get(1), 
				CTX.mapKey(Value.get("campaignDetails")), CTX.mapKey(Value.get("dataCube"))));
		
	}

	@Override
	public void run() {
        String campaign = randomCampaign();
        EventType event = randomEvent();
        System.out.println(String.format("Efent: %s on %s", event, campaign));
        addEvent(campaign, event);
		
	}


//	public void runCTXExample(AerospikeClient client) {
//		Key key = new Key(params.namespace, params.set, "ctx-ex");
//		String binName = params.getBinName("themap");
//
//		// Delete record if it already exists.
//		client.delete(params.writePolicy, key);
//
//		// Set up the example data
//		Map<Value,Value> core = new HashMap<Value,Value>();
//		Map<Value,Value> inner = new HashMap<Value,Value>();
//		Map<Value,Value> outer = new HashMap<Value,Value>();
//		core.put(Value.get("baz"), Value.get(3));
//		inner.put(Value.get("bar"), Value.get(core));
//		outer.put(Value.get("foo"),  Value.get(inner));
//
//		MapPolicy mapPolicy = new MapPolicy(MapOrder.KEY_ORDERED, (MapWriteFlags.CREATE_ONLY | MapWriteFlags.NO_FAIL));
//		Record record = client.operate(params.writePolicy, key,
//			MapOperation.putItems(mapPolicy, binName, outer),
//			MapOperation.increment(MapPolicy.Default, binName, Value.get("baz"), Value.get(5), CTX.mapKey(Value.get("foo")), CTX.mapKey(Value.get("bar")))
//		);
//
//		record = client.get(params.policy, key);
//		console.info("Record: " + record);
//	}
}
