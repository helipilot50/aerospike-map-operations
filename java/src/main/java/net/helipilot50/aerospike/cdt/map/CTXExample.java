/*
 * Copyright 2012-2019 Aerospike, Inc.
 *
 * Portions may be licensed to Aerospike, Inc. under one or more contributor
 * license agreements WHICH ARE COMPATIBLE WITH THE APACHE LICENSE, VERSION 2.0.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.aerospike.examples;

import java.util.HashMap;
import java.util.Map;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Operation;
import com.aerospike.client.Record;
import com.aerospike.client.Value;
import com.aerospike.client.cdt.CTX;
import com.aerospike.client.cdt.MapOperation;
import com.aerospike.client.cdt.MapOrder;
import com.aerospike.client.cdt.MapPolicy;
import com.aerospike.client.cdt.MapWriteFlags;

public class CTXExample extends Example {

	public CTXExample(Console console) {
		super(console);
	}

	/**
	 */
	@Override
	public void runExample(AerospikeClient client, Parameters params) {
		if (! params.hasCDTMap) {
			console.info("CDT map functions are not supported by the connected Aerospike server.");
			return;
		}
		runCTXExample(client, params);
	}

	/**
	 */
	public void runCTXExample(AerospikeClient client, Parameters params) {
		Key key = new Key(params.namespace, params.set, "ctx-ex");
		String binName = params.getBinName("themap");

		// Delete record if it already exists.
		client.delete(params.writePolicy, key);

		// Set up the example data
		Map<Value,Value> core = new HashMap<Value,Value>();
		Map<Value,Value> inner = new HashMap<Value,Value>();
		Map<Value,Value> outer = new HashMap<Value,Value>();
		core.put(Value.get("baz"), Value.get(3));
		inner.put(Value.get("bar"), Value.get(core));
		outer.put(Value.get("foo"),  Value.get(inner));

		MapPolicy mapPolicy = new MapPolicy(MapOrder.KEY_ORDERED, (MapWriteFlags.CREATE_ONLY | MapWriteFlags.NO_FAIL));
		Record record = client.operate(params.writePolicy, key,
			MapOperation.putItems(mapPolicy, binName, outer),
			MapOperation.increment(MapPolicy.Default, binName, Value.get("baz"), Value.get(5), CTX.mapKey(Value.get("foo")), CTX.mapKey(Value.get("bar")))
		);

		record = client.get(params.policy, key);
		console.info("Record: " + record);
	}
}
